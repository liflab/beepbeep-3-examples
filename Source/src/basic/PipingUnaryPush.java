/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2019 Sylvain Hallé

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
import ca.uqac.lif.cep.Doubler;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.UtilityMethods;
import ca.uqac.lif.cep.io.Print;

/**
 * Pipe processors together using the {@link ca.uqac.lif.cep.Connector Connector}
 * object. The chain of processors in this example can be represented
 * graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/PipingUnaryPush.png" alt="Processor graph">
 * <p>
 * This example works like {@link PipingBinary}, except that it operates in
 * <em>push</em> mode instead of <em>pull</em> mode.
 * <p>
 * The expected output of this program is:
 * <pre>
 * 0,2,4,6,8,10,12,14,
 * </pre>
 * @see PipingBinary
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class PipingUnaryPush
{
	public static void main (String[] args)
	{	
		/// Create an instance of the Doubler processor.
		Doubler doubler = new Doubler();
		
		/* Create a processor that simply prints to the console every event
		 * it receives */
		Print print = new Print();
		
		/* We now want to connect the output of doubler to the input of print.
		 * This connection is done by using static method connect() of the
		 * Connector object. */
		Connector.connect(doubler, print);
		
		/* Let us now push events to the doubler. */
		Pushable p = doubler.getPushableInput();
		for (int i = 0; i < 8; i++)
		{
			p.push(i);
			// Sleep a little in between so you have time to read
			UtilityMethods.pause(1000);
		}
		///
	}
}
