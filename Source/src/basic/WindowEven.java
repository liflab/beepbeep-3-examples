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
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Use a {@link ca.uqac.lif.cep.tmf.Window Window} processor to perform a
 * computation over a sliding window of events that are not numeric.
 * The chain of processors in this example can be
 * represented graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/WindowEven.png" alt="Processor graph">
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class WindowEven
{
	public static void main(String[] args)
	{
		/// Create a source of arbitrary numbers
		QueueSource source = new QueueSource().setEvents(2, 7, 1, 8, 2, 8, 1, 8, 2, 8);
		
		// Check if each number is even
		ApplyFunction is_even = new ApplyFunction(Numbers.isEven);
		Connector.connect(source, is_even);
		
		// Create a cumulate processor
		Cumulate sum = new Cumulate(
				new CumulativeFunction<Boolean>(Booleans.or));
		
		// Create a window processor of width 3, using sum as the
		// processor to be used on each window. Connect it to is_even.
		Window win = new Window(sum, 3);
		Connector.connect(is_even, win);
		
		// Pull events from the window
		Pullable p = win.getPullableOutput();
		for (int i = 0; i < 10; i++)
		{
			System.out.println("Window #" + i + ": " + p.pull());
		}
		///
	}
}
