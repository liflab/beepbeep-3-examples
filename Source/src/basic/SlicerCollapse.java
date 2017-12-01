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
package basic;

import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.numbers.Maximum;
import ca.uqac.lif.cep.util.Maps.Values;
import ca.uqac.lif.cep.util.CollectionUtils.RunOn;
import ca.uqac.lif.cep.tmf.ConstantProcessor;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Slicer;

/**
 * Apply an aggregation function on the output of a slicer.
 * <p>
 * You should first read {@link SlicerSimple}. This example starts in the
 * same way; however, it shows how the values of each slice can be aggregated.
 * This is done by first extracting the image (i.e. set of values in each
 * key-value pair) of the map, and then applying a
 * {@link ca.uqac.lif.cep.sets.CumulateOnSet CumulateOnSet} function on the
 * resulting set.
 * <p>
 * In this program, the slicing function is the identity, and the processor
 * given to the slicer is a simple counter that increments every time an event
 * is received. Since there is one such counter for each different input
 * event, the slicer effectively maintains the count of how many times each
 * value has been seen in its input stream. Graphically, this can be
 * represented as: 
 * <p>
 * <img src="{@docRoot}/doc-files/basic/SlicerCollapse.png" alt="Processor chain">
 * <p>
 * The expected output of this program is:
 * <pre>
 * 1.0
 * 1.0
 * 1.0
 * 1.0
 * 1.0
 * 2.0
 * 2.0
 * 3.0
 * 3.0
 * &hellip;
 * </pre>
 * @author Sylvain Hallé
 * @difficulty Medium
 */
public class SlicerCollapse 
{
	public static void main(String[] args)
	{
		/* We first setup a stream of numbers to be used as a source */
		QueueSource source = new QueueSource();
		source.setEvents(new Object[]{1, 6, 4, 3, 2, 1, 9});
		
		/* The function we'll use to create slices is the identity.
		 * This will create one distinct subtrace per number .*/
		Function slice_fct = new IdentityFunction(1);
		
		/* The processor chain to apply to each subtrace is a simple
		 * counter of events. */
		GroupProcessor counter = new GroupProcessor(1, 1);
		{
			ConstantProcessor to_one = new ConstantProcessor(new Constant(1));
			CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			Connector.connect(to_one, sum);
			counter.addProcessors(to_one, sum);
			counter.associateInput(INPUT, to_one, INPUT);
			counter.associateOutput(OUTPUT, sum, OUTPUT);
		}
		
		/* Create the slicer processor, by giving it the slicing function and
		 * the processor to apply on each slide. */
		Slicer slicer = new Slicer(slice_fct, counter);
		Connector.connect(source, slicer);
		
		/* Extract the image of the resulting map, by applying the
		 * MapValues function. The result is a Multiset of all the objects
		 * that occur as values in the input map. */
		FunctionProcessor map_values = new FunctionProcessor(Values.instance);
		Connector.connect(slicer, map_values);
		
		/* Apply the CumulateOnSet processor. This processor applies a
		 * cumulative function successively on every value of the input
		 * set. Here the function is Maximum, meaning that the resulting
		 * event is the maximum of all values in the input set. */
		RunOn max = new RunOn(new CumulativeProcessor(new CumulativeFunction<Number>(Maximum.instance)));
		Connector.connect(map_values, max);
		
		/* Let us now pull and print 10 events from the output. */
		Pullable p = max.getPullableOutput();
		for (int i = 0; i < 10; i++)
		{
			Object o = p.pull();
			System.out.println(o);
		}		
	}
}
