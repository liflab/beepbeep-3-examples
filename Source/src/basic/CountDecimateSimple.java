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
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.CountDecimate;

/**
 * Use the {@link ca.uqac.lif.cep.tmf.CountDecimate CountDecimate}
 * processor to discard events from a stream.
 * Graphically, this can be represented as follows:
 * <p>
 * <p>
 * The expected output of this program is:
 * <pre>
 * 0,3,6,9,
 * </pre>
 * @author Sylvain Hallé
 *
 */
public class CountDecimateSimple 
{
	public static void main(String[] args) 
	{
		/// We create a CountDecimate processor and instruct it to keep one
		// event every 3
		CountDecimate dec = new CountDecimate(3);
		
		// We connect dec to a Print processor
		Print print = new Print();
		Connector.connect(dec, print);
		
		// We get a hold of dec's Pushable object and push the integers
		// 0 to 9. 
		Pushable p = dec.getPushableInput();
		for (int i = 0; i < 10; i++)
		{
			p.push(i);
		}
		///
	}
}
