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
import ca.uqac.lif.cep.tmf.KeepLast;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Use the {@link ca.uqac.lif.cep.tmf.KeepLast KeepLast} processor to
 * keep the last event of an input stream.
 * The chain of processors in this example can be represented
 * graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/KeepLastPull.png" alt="Processor graph">
 * <p>
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class KeepLastPull
{
	public static void main(String[] args)
	{
	  ///
		QueueSource src = new QueueSource().setEvents(1, 2, 3, 4, 5);
		src.loop(false);
		KeepLast kl = new KeepLast();
		Connector.connect(src, kl);
		Pullable p = kl.getPullableOutput();
		while (p.hasNext())
		{
			Object o = p.next();
			System.out.print(o);
		}
		///
	}
}