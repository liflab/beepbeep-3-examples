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
import ca.uqac.lif.cep.UtilityMethods;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Usage of the {@link ca.uqac.lif.cep.util.Bags#contains Bags.contains}
 * function. This chain of processors can be represented graphically as
 * follows:
 * <p>
 * <img src="{@docRoot}/doc-files/util/BagsContains.png" alt="Processor graph">
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class BagsContains 
{
	public static void main(String[] args)
	{
		/// We create a QueueSource; note that this time, each event in
		// the source is itself a *list*
		QueueSource src1 = new QueueSource();
		src1.addEvent(UtilityMethods.createList(1f, 3f, 5f));
		src1.addEvent(UtilityMethods.createList(4f, 2f));
		src1.addEvent(UtilityMethods.createList(4f, 4f, 8f));
		src1.addEvent(UtilityMethods.createList(6f, 4f));
		// We create a second queue source of 1s
		QueueSource src2 = new QueueSource();
		src2.setEvents(1);
		
		// We then create a processor chain that will check if
		// the n-th list contains the number n
		ApplyFunction contains = new ApplyFunction(Bags.contains);
		Connector.connect(src1, 0, contains, 0);
		Cumulate counter = new Cumulate(
		    new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(src2, counter);
		Connector.connect(counter, 0, contains, 1);
		Pullable p = contains.getPullableOutput();
		for (int i = 0; i < 4; i++)
		{
			System.out.println(p.pull());
		}
		///
	}


}
