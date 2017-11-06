package widgets;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.numbers.Division;
import ca.uqac.lif.cep.tmf.Fork;

/**
 * Group processor that computes the cumulative average of a stream of
 * numbers. This processor is similar to the {@link basic.Average Average}
 * examples of the {@link basic} section. Graphically, it corresponds to the
 * following chain of processors:
 * <p>
 * <a href="{@docRoot}/doc-files/widgets/Average.png"><img
 *   src="{@docRoot}/doc-files/widgets/Average.png"
 *   alt="Processor graph"></a>
 * <p>
 * Since we encapsulate this chain into a 
 * {@link ca.uqac.lif.cep.GroupProcessor GroupProcessor}, in other processor
 * graphs that use it, it will be drawn as this:
 * <p>
 * <a href="{@docRoot}/doc-files/widgets/Average-box.png"><img
 *   src="{@docRoot}/doc-files/widgets/Average-box.png"
 *   alt="Processor graph"></a>
 * 
 * @author Sylvain Hallé
 * @see basic.Average Average
 * @difficulty Easy
 */
public class Average extends GroupProcessor 
{
	public Average()
	{
		super(1, 1);
		try
		{
			Fork fork = new Fork(2);
			associateInput(0, fork, 0);
			FunctionProcessor one = new FunctionProcessor(new Constant(1));
			Connector.connect(fork, BOTTOM, one, INPUT);
			CumulativeProcessor count = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			Connector.connect(one, count);
			CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			Connector.connect(fork, TOP, sum, INPUT);
			FunctionProcessor div = new FunctionProcessor(Division.instance);
			Connector.connect(sum, OUTPUT, div, TOP);
			Connector.connect(count, OUTPUT, div, BOTTOM);
			associateOutput(0, div, 0);
			addProcessors(fork, one, count, sum, div);
		}
		catch (ConnectorException e) 
		{
			// Do nothing
		}
	}
}
