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

import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Sets;

public class PutIntoExample
{
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		///
		QueueSource src = new QueueSource().setEvents("A", "B", "C", "D");
		Sets.PutInto put = new Sets.PutInto();
		Connector.connect(src, put);
		Pullable p = put.getPullableOutput();
		Set<Object> set1, set2;
		p.pull();
		set1 = (Set<Object>) p.pull();
		System.out.println("Set 1: " + set1);
		p.pull();
		set2 = (Set<Object>) p.pull();
		System.out.println("Set 2: " + set2);
		System.out.println("Set 1: " + set2);
		///
	}
}
