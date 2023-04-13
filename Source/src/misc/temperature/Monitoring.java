package misc.temperature;

import static ca.uqac.lif.cep.Connector.connect;

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.UniformProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionLambda;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.FilterOn;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tmf.SliceLast;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.Bags.RunOn;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.InstanceOf;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import misc.temperature.MonitoringEvent.TemperatureEvent;

public class Monitoring
{
  /* These are the same constants as in the original example */
  private static final double TEMPERATURE_THRESHOLD = 100;
  private static final int MAX_RACK_ID = 10;
  private static final long PAUSE = 100;
  private static final double TEMPERATURE_RATIO = 0.5;
  private static final double POWER_STD = 10;
  private static final double POWER_MEAN = 100;
  private static final double TEMP_STD = 20;
  private static final double TEMP_MEAN = 80;
  
  /* Additional parameters for our example */
  private static final int M = 2;
  private static final int N = 2;
  
  public static void main(String[] args)
  {
    FunctionLambda get_rack_id = new FunctionLambda((Object o) -> ((MonitoringEvent) o).getRackID()).setReturnType(Integer.class);
    FunctionLambda get_temperature = new FunctionLambda((Object o) -> ((TemperatureEvent) o).getTemperature()).setReturnType(Double.class);
    FunctionLambda get_timestamp = new FunctionLambda((Object o) -> ((MonitoringEvent) o).getTimestamp()).setReturnType(Float.class);
    
    MonitoringEventSource source = new MonitoringEventSource(MAX_RACK_ID, PAUSE, TEMPERATURE_RATIO, POWER_STD, POWER_MEAN, TEMP_STD, TEMP_MEAN);
    Pump pump = new Pump();
    FilterOn temp_events = new FilterOn(new InstanceOf(TemperatureEvent.class));
    GroupProcessor warning = new GroupProcessor(1, 1);
    {
      Window w = new Window(new Lists.PutInto(), N);
      Fork w_f = new Fork(3);
      connect(w, w_f);
      GroupProcessor all_above = new GroupProcessor(1, 1);
      {
        ApplyFunction above = new ApplyFunction(new FunctionLambda((Object o) -> ((TemperatureEvent) o).getTemperature() > TEMPERATURE_THRESHOLD).setReturnType(Boolean.class));
        Cumulate all = new Cumulate(new CumulativeFunction<Boolean>(Booleans.and));
        connect(above, all);
        all_above.associateInput(INPUT, above, INPUT);
        all_above.associateOutput(OUTPUT, all, OUTPUT);
        all_above.addProcessors(all, above);
      }
      RunOn l_all_above = new RunOn(all_above);
      connect(w_f, 0, l_all_above, INPUT);
      ApplyFunction condition = new ApplyFunction(new FunctionTree(Booleans.and,
          new FunctionTree(Numbers.isLessThan,
              new FunctionTree(Numbers.subtraction,
                  new FunctionTree(get_timestamp, new FunctionTree(new NthElement(N - 1), StreamVariable.Y)),
                  new FunctionTree(get_timestamp, new FunctionTree(new NthElement(N - 1), StreamVariable.Y))),
              new Constant(10)),
          StreamVariable.X));
      connect(l_all_above, OUTPUT, condition, 0);
      connect(w_f, 1, condition, 1);
      Filter filter = new Filter();
      connect(w_f, 2, filter, TOP);
      connect(condition, OUTPUT, filter, BOTTOM);
      GroupProcessor get_avg_temp = new GroupProcessor(1, 1);
      {
        ApplyFunction get_temp = new ApplyFunction(get_temperature);
        Fork f = new Fork();
        connect(get_temp, f);
        Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
        connect(f, TOP, sum, INPUT);
        TurnInto one = new TurnInto(1);
        connect(f, BOTTOM, one, INPUT);
        Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
        connect(one, sum_one);
        ApplyFunction div = new ApplyFunction(Numbers.division);
        connect(sum, OUTPUT, div, TOP);
        connect(sum_one, OUTPUT, div, BOTTOM);
        get_avg_temp.addProcessors(get_temp, f, sum, one, sum_one, div);
        get_avg_temp.associateInput(INPUT, get_temp, INPUT);
        get_avg_temp.associateOutput(OUTPUT, div, OUTPUT);
      }
      RunOn avg = new RunOn(get_avg_temp);
      connect(filter, avg);
      CreateWarning create_warning = new CreateWarning();
      connect(avg, create_warning);
      warning.addProcessors(w, w_f, l_all_above, condition, filter, get_avg_temp, create_warning);
      warning.associateInput(INPUT, w, INPUT);
      warning.associateOutput(OUTPUT, create_warning, OUTPUT);
    }
    SliceLast slice1 = new SliceLast(new FunctionLambda(
        (Object o) -> ((TemperatureEvent) o).getRackID()).setReturnType(Integer.class),
        warning);
    Fork fork1 = new Fork();
    connect(source, pump, temp_events, slice1);
    Print print1 = new Print();
    connect(slice1, print1);
    pump.run();
  }
  
  protected static class CreateWarning extends UniformProcessor
  {
    public CreateWarning()
    {
      super(1, 1);
    }

    @Override
    protected boolean compute(Object[] inputs, Object[] outputs)
    {
      int slice_id = ((Number) getContext("sliceID")).intValue();
      outputs[0] = new TemperatureWarning(slice_id, ((Number) inputs[0]).doubleValue());
      return true;
    }

    @Override
    public CreateWarning duplicate(boolean with_state)
    {
      return new CreateWarning();
    }
  }
  
  
  
}
