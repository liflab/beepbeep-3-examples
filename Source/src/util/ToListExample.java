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
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Bags;

/**
 * Create a stream of lists from the input of multiple processors,
 * using the {@link ca.uqac.cep.util.Bags.ToList ToList} function.
 * Graphically, this chain of processors can be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/util/ToListExample.png" alt="Processor graph">
 * <p>
 * The output of this program is:
 * <pre>
 * [3, 2, 1]
 * [1, 7, 1]
 * [4, 1, 2]
 * [1, 8, 3]
 * </pre>
 * @author Sylvain Hallé
 *
 */
public class ToListExample
{
	public static void main(String[] args) 
	{
		///
		QueueSource src1 = new QueueSource().setEvents(3, 1, 4, 1, 6);
		QueueSource src2 = new QueueSource().setEvents(2, 7, 1, 8);
		QueueSource src3 = new QueueSource().setEvents(1, 1, 2, 3, 5);
		ApplyFunction to_list = new ApplyFunction(
				new Bags.ToList(Number.class, Number.class, Number.class));
		Connector.connect(src1, 0, to_list, 0);
		Connector.connect(src2, 0, to_list, 1);
		Connector.connect(src3, 0, to_list, 2);
		Pullable p = to_list.getPullableOutput();
		for (int i = 0; i < 4; i++)
		{
			System.out.println(p.pull());
		}
		///
	}
}
