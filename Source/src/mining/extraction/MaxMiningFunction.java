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

import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.peg.Sequence;
import ca.uqac.lif.cep.peg.ml.SetMiningFunction;
import mining.SequenceReader;

/**
 * Create a mining function that computes the maximum value of all sequences.
 * @see MaxMiningFunctionProcessor
 * @author Sylvain Hallé
 */
public class MaxMiningFunction 
{
	public static void main(String[] args) throws FunctionException
	{
		/* First, we must get from somewhere a set of sequences. For the sake
		 * of this example, we just create a few dummy sequences of numbers
		 * from the contents of a file. */
		Set<Sequence<Number>> sequences = SequenceReader.readNumericalSequences("numbers-1.csv");
		
		/* We then create an instance of our mining function. A mining function
		 * takes as input a set of sequences, and returns for its output some
		 * "pattern" extracted from this set of sequences. */
		SumFunction a_f = new SumFunction();
		
		/* We then evaluate the function on our set of sequences. */
		Object[] outputs = new Object[1];
		a_f.evaluate(new Object[]{sequences}, outputs);
		
		/* In this case, the result of this function should be the sum of
		 * all values in all the input sequences. */
		System.out.println(outputs[0]);
	}
	
	public static class SumFunction extends SetMiningFunction<Number,Number>
	{
		public SumFunction() 
		{
			/* The constructor must simply specify that the output type
			 * of this function is a number. */
			super(Number.class);
		}

		@Override
		public Number mine(Set<Sequence<Number>> sequences) throws FunctionException 
		{
			/* The mine() method's job is to go trough all input sequences
			 * in the set, and to compute the "pattern" to extract from them.
			 * Here, the simple pattern we want to compute is the maximum of
			 * all values in all sequences. */
			float max = Float.MIN_VALUE;
			for (Sequence<Number> seq : sequences)
			{
				for (Number n : seq)
				{
					max = Math.max(max, n.floatValue());
				}	
			}
			return max;
		}
	}
}
