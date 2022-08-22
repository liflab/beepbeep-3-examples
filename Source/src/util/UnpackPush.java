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

import java.util.List;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.UtilityMethods;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.util.Lists;

/**
 * See the effect of using the 
 * {@link ca.uqac.lif.cep.util.Lists.Unpack Unpack} processor in push mode.
 * Graphically, this chain of processors can be represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/util/UnpackPush.png" alt="Processor graph">
*  <p>
 * The output of this program is:
 * <pre>
 * Before first push
 * 1,2,3,4,
 * Before second push
 * 5,6,7,
 * After second push
 * </pre>
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class UnpackPush 
{
	public static void main(String[] args) 
	{
		Lists.Unpack unpack = new Lists.Unpack();
		Print print = new Print();
		Connector.connect(unpack, print);
		Pushable p = unpack.getPushableInput();
		///
		List<Object> list = UtilityMethods.createList(1, 2, 3, 4);
		System.out.println("Before first push");
		p.push(list);
		list = UtilityMethods.createList(5, 6, 7);
		System.out.println("\nBefore second push");
		p.push(list);
		System.out.println("\nAfter second push");
		///
	}
}
