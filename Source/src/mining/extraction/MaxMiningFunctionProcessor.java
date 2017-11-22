package mining.extraction;

import java.util.Set;

import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.numbers.Maximum;
import ca.uqac.lif.cep.peg.ProcessorMiningFunction;
import ca.uqac.lif.cep.peg.Sequence;
import ca.uqac.lif.cep.sets.ProcessOnSet;
import mining.SequenceReader;

/**
 * Create a mining function from BeepBeep processors. This example is
 * identical to {@link MaxMiningFunction}, except that it uses BeepBeep
 * processors to perform the same mining task, instead of custom code.
 * As a result, the solution here is about 3 times shorter (in absolute
 * number of lines of code, roughly 5 vs. 15).
 * <p>
 * The function is parameterized as follows:
 * <p>
 * <table>
 * <tr><th>Parameter</th><th>Value</th></tr>
 * <tr>
 * <td><img src="{@docRoot}/doc-files/mining/trenddistance/BetaProcessor.png" alt="Processor graph"></td>
 * <td><img src="{@docRoot}/doc-files/mining/extraction/MaxProcessor.png" alt="Processor graph"></td>
 * </tr>
 * <tr>
 * <td><img src="{@docRoot}/doc-files/mining/extraction/AlphaProcessor.png" alt="Processor graph"></td>
 * <td><img src="{@docRoot}/doc-files/mining/extraction/MaxOnSet.png" alt="Processor graph"></td>
 * </tr>
 * </table>
 * <p>
 * As one can see, the trend processor computes the maximum value on a stream.
 * The aggregation processor runs yet another instance of the max processor,
 * by feeding the elements of the input set one by one and returning its
 * last value.
 * 
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
				new ProcessOnSet(new CumulativeProcessor(new CumulativeFunction<Number>(Maximum.instance))));
		
		/* We then evaluate the function on our set of sequences. */
		Number n = (Number) a_f.mine(sequences);
		
		/* In this case, the result of this function should be the maximum of
		 * all values in all the input sequences. */
		System.out.println(n);
	}
}
