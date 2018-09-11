/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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
package functions;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Booleans;

/**
 * Use a cumulative processor to perform the conjunction of
 * a stream of Boolean values.
 * Graphically, this chain of processors can be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/functionsthat /CumulateAnd.png" alt="Processor graph"> 
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class CumulateAnd
{
	public static void main(String[] args)
	{
		///
		QueueSource source = new QueueSource()
		    .setEvents(true, true, false, true, true);
		Cumulate and = new Cumulate(
				new CumulativeFunction<Boolean>(Booleans.and));
		Connector.connect(source, and);
		Pullable p = and.getPullableOutput();
		for (int i = 0; i < 5; i++)
		{
			System.out.println("The event is: " + p.pull());
		}
		///
	}

}
