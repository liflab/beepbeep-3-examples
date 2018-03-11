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
package util;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Lists;

/**
 * See the effect of chaining a {@link ca.uqac.lif.cep.util.Lists.Pack Pack}
 * processor to an {@link ca.uqac.lif.cep.util.Lists.Unpack Unpack} processor.
 * Graphically, this chain of processors can be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/util/PackUnpack.png" alt="Processor graph">
*  <p>
 * The output of this program is:
 * <pre>
 * 3
 * 1
 * 4
 * 1
 * 5
 * 9
 * &hellip;
 * </pre>
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class PackUnpack 
{

	public static void main(String[] args) 
	{
		///
		QueueSource src1 = new QueueSource();
		src1.setEvents(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
		QueueSource src2 = new QueueSource();
		src2.setEvents(false, true, false, false, false, true, false, true);
		Lists.Pack pack = new Lists.Pack();
		Connector.connect(src1, 0, pack, 0);
		Connector.connect(src2, 0, pack, 1);
		Lists.Unpack unpack = new Lists.Unpack();
		Connector.connect(pack, 0, unpack, 0);
		Pullable p = unpack.getPullableOutput();
		for (int i = 0; i < 6; i++)
		{
			System.out.println(p.pull());
		}
		///
	}
}
