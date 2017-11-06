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

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Pull events from the
 * {@link ca.uqac.lif.cep.tmf.QueueSource QueueSource} processor.
 * Graphically, the queue source is represented as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/QueueSourceUsage.png" alt="Processor graph">
 * <p>
 * Since it is a <em>source</em>, it has no input streams. We represent
 * in a rectangle the queue of events that the source will dispense.
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class QueueSourceUsage
{
	public static void main(String[] args)
	{
		// Create an empty queue source
		QueueSource source = new QueueSource();
		// Tell the source what events to output by giving it an array;
		// in this case, we output the first powers of 2
		source.setEvents(new Integer[]{1, 2, 4, 8, 16, 32});
		// Get a pullable to the source
		Pullable p = source.getPullableOutput();
		// Pull 8 events from the source. The queue source loops through
		// its array of events; hence after reaching the last (32), it
		// will restart from the beginning of its list.
		for (int i = 0; i < 8; i++)
		{
			// Method pull() returns an Object, hence we must manually cast
			// it as an integer (this is indeed what we get)
			int x = (Integer) p.pull();
			System.out.println("The event is: " + x);
		}
	}
}
