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

import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionException;

/**
 * Use the {@link ca.uqac.lif.cep.functions.StreamVariable ArgumentPlaceholder}
 * function to refer to the <i>i</i>-th argument of a function.
 * @author Sylvain Hallé
 */
public class PlaceholderUsage
{
	public static void main(String[] args) throws FunctionException
	{
		/* Create an instance of the ArgumentPlaceholder function. This
		 * function simply returns its <i>i</i>-th input argument, with
		 * the value <i>i</i> passed to the constructor. Indices start
		 * at zero. */
		Function foo = StreamVariable.Y;
		Object values[] = new Object[1];
		
		/* A constant does not need any argument; we may pass
		 * an empty array, or simply null. */
		Object inputs[] = new Object[]{42, "foo"};
		foo.evaluate(inputs, values);
		String s_value = (String) values[0]; // = "foo"
		System.out.printf("The value of foo is %s\n", s_value);
	}
}
