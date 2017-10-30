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

import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionException;

/**
 * Basic usage of the {@link ca.uqac.lif.cep.functions.Constant}
 * function.
 * @author Sylvain Hallé
 */
public class ConstantUsage
{
	public static void main(String[] args) throws FunctionException
	{
		/* Create a new function that will return the same thing,
		 * whatever its arguments. This is done by instantiating a Constant
		 * object. The constructor of this class takes as input the object
		 * that will be the return value of the function. Here, our constant
		 * function will always return the string "foo". */
		Function foo = new Constant("foo");
		
		/* A constant does not need any argument; we may pass
		 * an empty array, or simply null. */
		Object[] values = new Object[1];
		foo.evaluate(null, values);
		String s_value = (String) values[0]; // = "foo"
		System.out.printf("The value of foo is %s\n", s_value);

		/* Let's create another constant function, this time returning the
		 * number 1 every time it is called. */
		Function one = new Constant(1);
		one.evaluate(null, values);
		int i_value = (Integer) values[0]; // = 1
		System.out.printf("The value of one is %d\n", i_value);
	}
}
