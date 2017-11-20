package mining;

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ArgumentPlaceholder;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.ConstantProcessor;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.numbers.AbsoluteValue;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.numbers.Division;
import ca.uqac.lif.cep.numbers.IsLessThan;
import ca.uqac.lif.cep.numbers.Subtraction;
import ca.uqac.lif.cep.peg.TooFarFromTrend;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;

public class AverageValue 
{
	public static void main(String[] args) throws ConnectorException
	{
		GroupProcessor average = new GroupProcessor(1, 1);
		{
			Fork fork = new Fork(2);
			average.associateInput(INPUT, fork, INPUT);
			CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			Connector.connect(fork, TOP, sum, INPUT);
			ConstantProcessor one = new ConstantProcessor(new Constant(1));
			Connector.connect(fork, BOTTOM, one, INPUT);
			CumulativeProcessor sum_one = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			Connector.connect(one, sum_one);
			FunctionProcessor div = new FunctionProcessor(Division.instance);
			Connector.connect(sum, OUTPUT, div, TOP);
			Connector.connect(sum_one, OUTPUT, div, BOTTOM);
			average.associateOutput(OUTPUT, div, OUTPUT);
			average.addProcessors(fork, sum, one, sum_one, div);
		}
		TooFarFromTrend<Number,Number,Number> alarm = new TooFarFromTrend<Number,Number,Number>(6, 3, average, new FunctionTree(AbsoluteValue.instance, 
				new FunctionTree(Subtraction.instance, new ArgumentPlaceholder(0), new ArgumentPlaceholder(1))), 0.5, IsLessThan.instance);
		QueueSource source = new QueueSource();
		source.setEvents(new Object[]{6.1, 5.9, 6, 6.7, 6.7, 6.7});
		Connector.connect(source, alarm);
		Pullable p = alarm.getPullableOutput();
		boolean b = true;
		for (int i = 0; b && i < 10; i++)
		{
			b = (Boolean) p.pull();
			System.out.println(b);
		}
	}
}
