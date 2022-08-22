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
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Apply a processor on collections of events 
 * using the {@link ca.uqac.cep.util.Bags.RunOn RunOn} processor.
 * Graphically, this chain of processors can be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/util/RunOnExample.png" alt="Processor graph">
 * <p>
 * The output of this program is:
 * <pre>
 * 9.0
 * 6.0
 * 16.0
 * 10.0
 * </pre>
 * @author Sylvain Hallé
 *
 */
public class RunOnExample
{
	public static void main(String[] args) 
	{
		///
		QueueSource src1 = new QueueSource();
		src1.addEvent(UtilityMethods.createList(1f, 3f, 5f));
		src1.addEvent(UtilityMethods.createList(4f, 2f));
		src1.addEvent(UtilityMethods.createList(4f, 4f, 8f));
		src1.addEvent(UtilityMethods.createList(6f, 4f));
		Bags.RunOn run = new Bags.RunOn(new Cumulate(
		    new CumulativeFunction<Number>(Numbers.addition)));
		Connector.connect(src1, run);
		Pullable p = run.getPullableOutput();
		for (int i = 0; i < 4; i++)
		{
			System.out.println(p.pull());
		}
		///
	}
}
