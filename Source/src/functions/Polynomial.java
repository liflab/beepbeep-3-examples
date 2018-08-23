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

import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.PassthroughFunction;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Use an {@link StreamVariable} and a {@link FunctionTree} to create
 * the polynomial function <i>x</i><sup>2</sup>+3.
 * 
 * @author Sylvain Hallé
 */
public class Polynomial extends PassthroughFunction
{
	/*
	 * We need to implement the getFunction() method, which must
	 * return an object of type Function. Here, we create a FunctionTree
	 * that computes x^2+3.
	 */
	@Override
	public Function getFunction()
	{
		/* At the top level, the polynomial is an addition of two
		 * terms. Hence the top of the function tree is the function
		 * Addition, followed by two arguments. */
		return new FunctionTree(Numbers.addition,
				/* The first argument is itself a FunctionTree that computes
				 * the square of the argument. */
				new FunctionTree(Numbers.power,
					/* This is done by using the Power function. StreamVariable.X
					 * refers by default to the first argument passed to a function. */
					StreamVariable.X,
					new Constant(2)),
				/* The second term of the addition is the constant 3 */
				new Constant(3));
	}

	/*
	 * Small main() to illustrate the concept
	 */
	public static void main(String[] args) throws FunctionException
	{
		Polynomial poly = new Polynomial();
		Object[] value = new Object[1];
		poly.evaluate(new Integer[]{3}, value);
		System.out.printf("Return value of the function: %f\n", value[0]);
		poly.evaluate(new Integer[]{8}, value);
		System.out.printf("Return value of the function: %f\n", value[0]);
	}
}
