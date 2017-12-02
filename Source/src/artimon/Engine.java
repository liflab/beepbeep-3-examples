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
package artimon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Strings;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.tmf.Trim;

/**
 * Compute data about the execution of a hybrid car engine. In this example,
 * sensors collect data about the execution of a car engine, such as
 * electrical power remaining, fuel remaining, speed and distance travelled.
 * The execution of the engine is broken down into extended periods of time
 * called "cycles", identified in the input data by a cycle number.
 * <p>The processor chain in this example computes, for each cycle, the
 * <em>recovery rate</em>. This corresponds to the sum of all datapoints 
 * in each cycle where electrical power is negative. The chain of processors
 * that performs this computation can be illustrated as follows: 
 * <p>
 * <img src="{@docRoot}/doc-files/artimon/Engine.png" alt="Processor chain">
 * @author Sylvain Hallé (for the BeepBeep processor chain)
 * @author Nicolas Rapin (for the original example)
 */
@SuppressWarnings("unused")
public class Engine
{
	/* Let us define constants for each column index */
	static final int TIME = 0, NO_CYCLE = 1, DISTANCE = 2, PUISSANCE_ELEC = 3,
			SOC_ELEC = 4, PUISSANCE_FUEL = 5, SOC_FUEL = 6, SPEED = 7;
	
	/* A constant used in the computation, called "cbat" */
	static final int CBAT = 16021800;
	
	public static void main(String[] args) throws FileNotFoundException
	{
		/* We put the filename in a variable */
		String filename = "simple.csv";
		
		/* We create a line reader which will read from an input stream. In the
		 * present case, the stream is obtained from the file we specified. */
		ReadLines reader = new ReadLines(Engine.class.getResourceAsStream(filename));
		
		/* The first line of the file is a comment line, so we ignore it */
		Trim first_line = new Trim(1);
		Connector.connect(reader, first_line);
		
		/* We create an array feeder, which will read individual lines of text
		 * and turn them into an array of primitive values. */
		ApplyFunction array_feeder = new ApplyFunction(new Strings.SplitString(";"));
		Connector.connect(first_line, array_feeder);
		
		/* Let us fork the stream of input tuples in two parts */
		Fork fork = new Fork(2);
		Connector.connect(array_feeder, fork);
		
		/* We create a sub-processor that will add all values of the
		 * "puissance_elec" attribute, but only if it is negative. */
		GroupProcessor add_negative_pe = new GroupProcessor(1, 1);
		{
			ApplyFunction get_pe = new ApplyFunction(new NthElement(PUISSANCE_ELEC));
			Fork anp_fork = new Fork(2);
			Connector.connect(get_pe, anp_fork);
			ApplyFunction is_negative = new ApplyFunction(
				new FunctionTree(Numbers.isLessThan, 
						StreamVariable.X,
						Constant.ZERO));
			Filter filter = new Filter();
			Connector.connect(anp_fork, 0, filter, TOP);
			Connector.connect(anp_fork, 1, is_negative, INPUT);
			Connector.connect(is_negative, OUTPUT, filter, BOTTOM);
			CumulativeProcessor sum_of_negatives = new CumulativeProcessor(new CumulativeFunction<Number>(Numbers.addition));
			Connector.connect(filter, sum_of_negatives);
			ApplyFunction divide = new ApplyFunction(new FunctionTree(Numbers.division, StreamVariable.X, new Constant(CBAT)));
			Connector.connect(sum_of_negatives, divide);
			add_negative_pe.addProcessors(get_pe, anp_fork, is_negative, filter, sum_of_negatives, divide);
			add_negative_pe.associateInput(INPUT, get_pe, INPUT);
			add_negative_pe.associateOutput(OUTPUT, divide, OUTPUT);			
		}
		
		/* We create a slicer according to the cycle number, and connect it to
		 * the first output of the fork. */
		Slice slicer = new Slice(new NthElement(NO_CYCLE), add_negative_pe);
		Connector.connect(fork, TOP, slicer, INPUT);
		
		/* In the second path, we determine when a cycle ends. This
		 * is done by checking if two successive events have the same value
		 * for their "cycle" attribute. */
		ApplyFunction get_cycle_nb = new ApplyFunction(new NthElement(NO_CYCLE));
		Connector.connect(fork, BOTTOM, get_cycle_nb, INPUT);
		Fork cycle_fork = new Fork(2);
		Connector.connect(get_cycle_nb, cycle_fork);
		Trim cycle_trim = new Trim(1);
		Connector.connect(cycle_fork, BOTTOM, cycle_trim, INPUT);
		ApplyFunction cycle_change = new ApplyFunction(Equals.instance);
		Connector.connect(cycle_fork, 0, cycle_change, TOP);
		Connector.connect(cycle_trim, OUTPUT, cycle_change, BOTTOM);
		ApplyFunction not = new ApplyFunction(Booleans.not);
		Connector.connect(cycle_change, not);
		
		/* We merge the two paths into a filter. */
		Filter out_if_cycle_changes = new Filter();
		Connector.connect(slicer, OUTPUT, out_if_cycle_changes, TOP);
		Connector.connect(not, OUTPUT, out_if_cycle_changes, BOTTOM);
		
		/* Let's pull events! */
		Pullable p = out_if_cycle_changes.getPullableOutput();
		long start = System.currentTimeMillis();
		while (true)
		{
			Object o = p.next();
			if (o == null)
				break;
			System.out.println(o);
		}
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000);
	}
}
