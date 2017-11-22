/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
