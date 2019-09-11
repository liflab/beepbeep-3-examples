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
package functions;

import java.util.Set;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Show the basic usage of
 * {@link ca.uqac.lif.cep.functions.Function Function} objects.
 * {@link ca.uqac.lif.cep.functions.Function#evaluate(Object[], Object[])
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class FunctionUsage
{
	public static void main(String[] args)
	{
		/// We create an instance of the negation function. The class
		// Booleans contains a static reference to an instance of that function
		Function negation = Booleans.not;

		// To hold the return value of a function, we must give it an array
		Object[] out = new Object[1];

		// Let us evaluate the negation of a Boolean value. This is done
		// by calling evaluate on the function object.
		negation.evaluate(new Object[]{true}, out);

		// We print the return value of the function
		System.out.println("The return value of the function is: " + out[0]);
		///

		//* Let us now try the same thing with a function that takes
		// two arguments.
		Function addition = Numbers.addition;

		// Since addition takes two arguments, we must pass it an array of
		// two objects
		addition.evaluate(new Object[]{2, 3}, out);

		// We print the return value of the function
		System.out.println("The return value of the function is: " + out[0]);
		//*
		
		//# Finally, a function does not necessarily have an output arity of 1.
		// In this example, we call a function that has both an input and an
		// output arity of 2. From two numbers x and y, it outputs the
		// quotient and the remainder of the division of x by y.
		Function int_division = IntegerDivision.instance;
		Object[] outs = new Object[2];
		int_division.evaluate(new Object[]{14, 3}, outs);
		System.out.println("14 divided by 3 equals " +
				outs[0] + " remainder " + outs[1]);
		//#
	}
	
	/**
	 * A function that computes integer division. From two numbers x and y,
	 * it outputs the quotient and the remainder of the division of x by y.
	 * This function is used in the code example above to show that functions
	 * can have an output arity different from 1.
	 */
	public static class IntegerDivision extends Function
	{
		/**
		 * A reference to a single publicly accessible instance of the
		 * function
		 */
		public static final transient IntegerDivision instance = new IntegerDivision();
		
		/**
		 * The constructor is declared private on purpose
		 */
		private IntegerDivision()
		{
			super();
		}
		
		@Override
		public Function duplicate(boolean with_state) 
		{
			return instance;
		}

		@Override
		public void evaluate(Object[] inputs, Object[] outputs, Context c) 
		{
			int x = (Integer) inputs[0];
			int y = (Integer) inputs[1];
			outputs[0] = x / y;
			outputs[1] = x % y;
		}

		@Override
		public int getInputArity()
		{
			return 2;
		}

		@Override
		public int getOutputArity()
		{
			return 2;
		}

		@Override
		public void reset() 
		{
			super.reset();
		}

		@Override
		public void getInputTypesFor(Set<Class<?>> classes, int index) 
		{
			classes.add(Number.class);
		}

		@Override
		public Class<?> getOutputTypeFor(int index) 
		{
			return Integer.class;
		}
	}
}
