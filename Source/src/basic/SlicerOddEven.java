/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Use the {@link ca.uqac.lif.cep.tmf.Slice SlicerMap} to compute the
 * sum of odd and even numbers separately.
 * Graphically, this can be represented as: 
 * <p>
 * <img src="{@docRoot}/doc-files/basic/SlicerOddEven.png" alt="Processor chain">
 * <p>
 * The expected output of this program is:
 * <pre>
 * {1=1.0}
 * {1=1.0, 6=1.0}
 * {1=1.0, 4=1.0, 6=1.0}
 * {1=1.0, 3=1.0, 4=1.0, 6=1.0}
 * {1=1.0, 2=1.0, 3=1.0, 4=1.0, 6=1.0}
 * {1=2.0, 2=1.0, 3=1.0, 4=1.0, 6=1.0}
 * {1=2.0, 2=1.0, 3=1.0, 4=1.0, 6=1.0, 9=1.0}
 * &hellip;
 * </pre>
 * @author Sylvain Hallé
 * @see SlicerSimple
 * @difficulty Easy
 */
public class SlicerOddEven 
{
	public static void main(String[] args)
	{
		/// We first setup a stream of numbers to be used as a source
		QueueSource source = new QueueSource();
		source.setEvents(1, 6, 4, 3, 2, 1, 9);
		
		/* The function we'll use to create slices is the identity.
		 * This will create one distinct subtrace per number .*/
		Function slicing_fct = new IdentityFunction(1);
		
		/* The processor chain to apply to each subtrace is a simple
		 * counter of events. */
		GroupProcessor counter = new GroupProcessor(1, 1);
		{
			TurnInto to_one = new TurnInto(new Constant(1));
			Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
			Connector.connect(to_one, sum);
			counter.addProcessors(to_one, sum);
			counter.associateInput(INPUT, to_one, INPUT);
			counter.associateOutput(OUTPUT, sum, OUTPUT);
		}
		
		/* Create the slicer processor, by giving it the slicing function and
		 * the processor to apply on each slide. */
		Slice slicer = new Slice(slicing_fct, counter);
		Connector.connect(source, slicer);
		
		/* Let us now pull and print 10 events from the slicer. */
		Pullable p = slicer.getPullableOutput();
		for (int i = 0; i < 10; i++)
		{
			Object o = p.pull();
			System.out.println(o);
		}
		///
	}
}
