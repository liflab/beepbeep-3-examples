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
import ca.uqac.lif.cep.util.Maps;

/**
 * Use the {@link ca.uqac.lif.cep.util.Maps.PutInto PutInto} processor
 * to update a map from two input streams.
 * <p>
 * The output of this program is:
 * <pre>
 * {foo=1}
 * {bar=abc, foo=1}
 * {bar=abc, foo=def}
 * {bar=abc, foo=def, baz=6}
 * </pre>
 * @author Sylvain Hallé
 */
public class MapPutInto
{
	public static void main(String[] args)
	{
		///
		QueueSource keys = new QueueSource().setEvents("foo", "bar", "foo", "baz");
		QueueSource values = new QueueSource().setEvents(1, "abc", "def", 6);
		Maps.PutInto put = new Maps.PutInto();
		Connector.connect(keys, 0, put, 0);
		Connector.connect(values, 0, put, 1);
		Pullable p = put.getPullableOutput();
		for (int i = 0; i < 4; i++)
		{
			System.out.println(p.pull());
		}
		///
	}
}
