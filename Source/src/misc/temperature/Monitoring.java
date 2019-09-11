package misc.temperature;

import static ca.uqac.lif.cep.Connector.connect;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunctionLambda;
import ca.uqac.lif.cep.tmf.FilterOn;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.util.InstanceOf;
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
  
  public static void main(String[] args)
  {
    MonitoringEventSource source = new MonitoringEventSource(MAX_RACK_ID, PAUSE, TEMPERATURE_RATIO, POWER_STD, POWER_MEAN, TEMP_STD, TEMP_MEAN);
    Pump pump = new Pump();
    FilterOn temp_events = new FilterOn(new InstanceOf(TemperatureEvent.class));
    GroupProcessor warning = new GroupProcessor(1, 1);
    {
      
    }
    SliceLast slice1 = new SliceLast(new ApplyFunctionLambda((Object o) -> ((TemperatureEvent) o).getRackId(), Integer.class),
        warning); 
    Fork fork = new Fork();
    connect(source, pump, temp_events, get_temp, fork);
    
  }
  
  
  
}
