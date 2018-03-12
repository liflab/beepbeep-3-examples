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

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Print events from a queue source without the use of a pump.
 * @author Sylvain Hallé
 *
 */
public class WithoutPump 
{

	public static void main(String[] args) throws InterruptedException
	{
		///
		QueueSource source = new QueueSource().setEvents(1, 2, 3, 4);
		Print print = new Print();
		Pullable pl = source.getPullableOutput();
		Pushable ps = print.getPushableInput();
		while (true)
		{
			ps.push(pl.pull());
			Thread.sleep(1000);
		}
		///
	}
}
