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

import ca.uqac.lif.cep.UtilityMethods;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Illustrate the use of a few functions from the
 * {@link ca.uqac.lif.cep.util.Bags Bags} class.
 * @author Sylvain Hallé
 */
public class BagsFunctions 
{
	public static void main(String[] args)
	{
		//1
		List<Object> list = UtilityMethods.createList(-3, 6, -1, -2);
		Object[] out = new Object[1];
		Function f = new Bags.ApplyToAll(Numbers.absoluteValue);
		f.evaluate(new Object[]{list}, out);
		System.out.println(out[0]);
		//1
		//2
		Function filter = new Bags.FilterElements(Numbers.isEven);
		filter.evaluate(new Object[]{list}, out);
		System.out.println(out[0]);
		//2
	}

}
