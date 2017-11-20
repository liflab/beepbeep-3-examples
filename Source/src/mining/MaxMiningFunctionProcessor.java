package mining;

import java.util.Set;

import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.numbers.Maximum;
import ca.uqac.lif.cep.peg.ProcessorMiningFunction;
import ca.uqac.lif.cep.peg.Sequence;

/**
 * Create a mining function from BeepBeep processors. This example is
 * identical to {@link MaxMiningFunction}, except that it uses BeepBeep
 * processors to perform the same mining task, instead of custom code.
 * As a result, the solution here is about 3 times shorter (in absolute
 * number of lines of code, roughly 5 vs. 15).
 * @see MaxMiningFunction
 * @author Sylvain Hallé
 */
public class MaxMiningFunctionProcessor 
{
	public static void main(String[] args) throws FunctionException, ConnectorException
	{
		/* First, we must get from somewhere a set of sequences. For the sake
		 * of this example, we just create a few dummy sequences of numbers
		 * from the contents of a file. */
		Set<Sequence<Number>> sequences = SequenceReader.readNumericalSequences("numbers-1.csv");
		
		/* We then create an instance of our mining function. A mining function
		 * takes as input a set of sequences, and returns for its output some
		 * "pattern" extracted from this set of sequences. */
		ProcessorMiningFunction<Number,Number> a_f = new ProcessorMiningFunction<Number,Number>(
				new CumulativeProcessor(new CumulativeFunction<Number>(Maximum.instance)), 
				new CumulativeProcessor(new CumulativeFunction<Number>(Maximum.instance)));
		
		/* We then evaluate the function on our set of sequences. */
		Object[] outputs = new Object[1];
		a_f.evaluate(new Object[]{sequences}, outputs);
		
		/* In this case, the result of this function should be the maximum of
		 * all values in all the input sequences. */
		System.out.println(outputs[0]);
	}
}
