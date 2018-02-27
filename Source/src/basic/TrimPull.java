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
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Trim;

/**
 * Use the {@link ca.uqac.lif.cep.tmf.Trim Trim} processor to discard events
 * from the beginning of a stream.
 * The chain of processors in this example can be represented
 * graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/TrimPull.png" alt="Processor graph">
 * @see PipingBinary
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class TrimPull
{
	public static void main (String[] args)
	{
		/// Create a source of arbitrary numbers
		QueueSource source = new QueueSource().setEvents(1, 2, 3, 4, 5, 6);
		
		/* Create an instance of the Trim processor. This processor is
		 * instructed to discard the first 3 events it receives. */
		Trim trim = new Trim(3);
		
		/* Connect the trim processor to the output of the source. */
		Connector.connect(source, trim);
		
		/* Let us now print what we receive by pulling on the output of
		 * doubler. */
		Pullable p = trim.getPullableOutput();
		for (int i = 0; i < 6; i++)
		{
			int x = (Integer) p.pull();
			System.out.println("The event is: " + x);
		}
		///
	}
}
