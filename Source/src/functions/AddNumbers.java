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

import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.numbers.Addition;

/**
 * Add two numbers with the
 * {@link ca.uqac.lif.cep.numbers.Addition Addition} function.
 * 
 * @author Sylvain Hallé
 */
public class AddNumbers
{
	public static void main(String[] args) throws FunctionException
	{
		/* Get a refernce to the Addition function. The constructor
		 * of this class is private; the only way to get an instance of
		 * this object is through its public static field called "instance".
		 * This ensures that only one instance of this object is ever used
		 * in any program. In other words, every time you want to use
		 * Addition somewhere, you always use the same object. 
		 * 
		 * For more information on this matter, see {@link IsPrime#instance}. */
		Function add = Numbers.addition;
		
		/* A function receives its arguments through an array of objects.
		 * In the case of Addition, two arguments are required, so we create
		 * an array of size 2 containing two numbers. */
		Object[] inputs = new Object[]{2, 3};
		
		/* A function returns a value by putting objects inside another
		 * array. The function Addition returns one value, so we create
		 * an empty array of size 1. */
		Object[] values = new Object[1];
		
		/* We are now ready to evaluate the sum of 2 and 3. Each Function
		 * object has a method called "evaluate" that takes an array of
		 * inputs, and an array of outputs. */
		add.evaluate(inputs, values);
		
		/* After the call, the sum of 2 and 3 is contained in the (only)
		 * element of the output array. Let's print it. */
		float value = (Float) values[0]; // = 5
		System.out.printf("The value is %f\n", value);
	}
}
