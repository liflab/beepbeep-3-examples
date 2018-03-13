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
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Print events from a queue source with the use of a
 * {@link ca.uqac.lif.cep.tmf.Pump Pump} processor.
 * @author Sylvain Hallé
 *
 */
public class WithPump 
{

	public static void main(String[] args) throws InterruptedException
	{
		///
		QueueSource source = new QueueSource().setEvents(1, 2, 3, 4);
		Pump pump = new Pump(1000);
		Print print = new Print();
		Connector.connect(source, pump, print);
		Thread th = new Thread(pump);
		th.start();
		///
	}
}
