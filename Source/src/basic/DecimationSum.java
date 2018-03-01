/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.LEFT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.RIGHT;
import static ca.uqac.lif.cep.Connector.connect;
import util.UtilityMethods;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Compute the sum of every pair of successive events. Here we are
 * interested in computing the sum of events at position 0-1, 2-3, 4-5, etc.
 * To do so, we compute first the sum of every two successive events,
 * and then keep every other event of the resulting trace.
 * Graphically, this chain of processors can be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/Decimation.png" alt="Processor graph">
 * <p>
 * On the input stream 6, 5, 3, 8, 9, 2, 1, 7, 4, 5, &hellip;,
 * the expected output of this program is:
 * <pre>
 * Event #0 is: 11
 * Event #1 is: 11
 * Event #2 is: 11
 * Event #3 is: 8
 * Event #4 is: 9
 * Event #5 is: 6
 * Event #6 is: 13
 * Event #7 is: 20
 * Event #8 is: 7
 * </pre>
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class DecimationSum
{
	public static void main(String[] args)
	{
		/* Create a stream of dummy values. */
		QueueSource source_values = new QueueSource();
		source_values.setEvents(6, 5, 3, 8, 9, 2, 1, 7, 4, 5,
				2, 4, 7, 6, 12, 8, 1);
		
		/* Duplicate this stream into two paths. */
		Fork fork = new Fork(2);
		connect(source_values, fork);
		
		/* The first path is plugged directly as the first argument of
		 * an adding processor. */
		ApplyFunction sum = new ApplyFunction(Numbers.addition);
		connect(fork, LEFT, sum, LEFT);
		
		/* Along the second path, we start by removing the first event of
		 * the stream. This is done using the Trim processor. */
		Trim trim = new Trim(1);
		connect(fork, RIGHT, trim, INPUT);
		
		/* We then plug the output of this Trim processor as the second
		 * argument of the adding processor. This has for effect that the
		 * sum processor will add events of the input stream at positions
		 * i and i+1 (for i = 0, 1, 2, etc.). From the stream of numbers
		 * defined above, the output of this processor will be:
		 * 6+5, 5+3, 3+8, 8+9, etc. */
		connect(trim, OUTPUT, sum, RIGHT);
		
		/* Since we want to keep only the sums of events at positions 0-1,
		 * 2-3, 4-5, etc., we have to remove from this output every other
		 * event. This is done with the CountDecimate processor, which is
		 * instructed to keep every <i>n</i>-th event it receives; here, n=2. */
		CountDecimate decimate = new CountDecimate(2);
		connect(sum, OUTPUT, decimate, INPUT);
		
		/* Get the Pullable of this last processor, and show the first few
		 * values it outputs. */
		Pullable p = decimate.getPullableOutput();
		for (int i = 0; i < 9; i++)
		{
			int v = ((Number) p.pull()).intValue();
			System.out.printf("Event #%d is: %d\n", i, v);
			// Pause between each so you have time to read!
			UtilityMethods.pause(1000);
		}
	}
}
