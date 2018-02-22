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
package mining;

import java.util.Set;

import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.peg.ProcessorMiningFunction;
import ca.uqac.lif.cep.peg.Sequence;
import ca.uqac.lif.cep.tmf.ReplaceWith;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Create a mining function from BeepBeep processors.
 * @see AverageMiningFunction
 * @author Sylvain Hallé
 * @difficulty Medium
 */
public class PatternMiningFunctionProcessor 
{
	public static void main(String[] args) throws FunctionException
	{
		/* First, we must get from somewhere a set of sequences. For the sake
		 * of this example, we just create a few dummy sequences of numbers
		 * from the contents of a file. */
		Set<Sequence<Number>> sequences = SequenceReader.readNumericalSequences("numbers-1.csv");
		
		/* We create a processor that computes the number of times a value
		 * is followed by the same value. */
		GroupProcessor total_same = new GroupProcessor(1, 1);
		{
			Fork fork = new Fork(3);
			Trim trim = new Trim(1);
			Connector.connect(fork, 0, trim, INPUT);
			ApplyFunction equals = new ApplyFunction(Equals.instance);
			Connector.connect(fork, 1, equals, BOTTOM);
			Connector.connect(trim, OUTPUT, equals, TOP);
			ReplaceWith ones = new ReplaceWith(new Constant(1));
			Connector.connect(fork, 2, ones, INPUT);
			Filter filter = new Filter();
			Connector.connect(ones, OUTPUT, filter, TOP);
			Connector.connect(equals, OUTPUT, filter, BOTTOM);
			Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
			Connector.connect(filter, sum);
			total_same.associateInput(INPUT, fork, INPUT);
			total_same.associateOutput(OUTPUT, sum, OUTPUT);
			total_same.addProcessors(fork, sum, trim, ones, equals, filter);
		}
		
		/* We then create an instance of our mining function. A mining function
		 * takes as input a set of sequences, and returns for its output some
		 * "pattern" extracted from this set of sequences. */
		ProcessorMiningFunction<Number,Number> a_f = new ProcessorMiningFunction<Number,Number>(total_same, new Cumulate(new CumulativeFunction<Number>(Numbers.maximum)), 0);
		
		/* We then evaluate the function on our set of sequences. */
		Object[] outputs = new Object[1];
		a_f.evaluate(new Object[]{sequences}, outputs);
		
		/* In this case, the result of this function should be the maximum
		 * number of times the same value has been seen twice in a single
		 * input sequence. */
		System.out.println(outputs[0]);
	}
	
}
