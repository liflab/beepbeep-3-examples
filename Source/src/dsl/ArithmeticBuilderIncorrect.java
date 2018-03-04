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
package dsl;

import java.util.ArrayDeque;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.dsl.GrammarObjectBuilder;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Shows what happens when a
 * {@link ca.uqac.lif.cep.dsl.GrammarObjectBuilder GrammarObjectBuilder}
 * incorrectly manipulates its object stack.
 * @author Sylvain Hallé
 * @see ArithmeticBuilder
 * @difficulty Medium
 */
public class ArithmeticBuilderIncorrect extends GrammarObjectBuilder<Function>
{
	public static void main(String[] args) throws BuildException
	{
		//&
		ArithmeticBuilderIncorrect builder = new ArithmeticBuilderIncorrect();
		Function f = builder.build("+ 3 - 4 5");
		Object[] value = new Object[1];
		f.evaluate(new Object[]{}, value);
		System.out.println(value[0]);
		//&
	}
	
	
	///
	public ArithmeticBuilderIncorrect()
	{
		super();
		try
		{
			setGrammar("<exp> := <add> | <sbt> | <num>;\n"
					+ "<add> := + <exp> <exp>;\n"
					+ "<sbt> := - <exp> <exp>;\n"
					+ "<num> := ^[0-9]+;");
		}
		catch (InvalidGrammarException e)
		{
			// Do nothing
		}
	}
	///

	@Builds(rule="<num>")
	//*
	public void handleNum(ArrayDeque<Object> stack)
	{
		String s_num = (String) stack.pop();
		Number n_num = Float.parseFloat(s_num);
		Constant c = new Constant(n_num);
		stack.push(c);
	}
	//*

	//%
	@Builds(rule="<add>")
	public void handleAdd(ArrayDeque<Object> stack)
	{
		Function f2 = (Function) stack.pop();
		stack.pop(); // Incorrect! This line and the next should be swapped
		Function f1 = (Function) stack.pop();
		stack.push(new FunctionTree(Numbers.addition, f1, f2));
	}
	//%

	//!
	@Builds(rule="<sbt>")
	public void handleSbt(ArrayDeque<Object> stack)
	{
		Function f2 = (Function) stack.pop();
		Function f1 = (Function) stack.pop();
		stack.pop(); // To remove the "-" symbol
		stack.push(new FunctionTree(Numbers.subtraction, f1, f2));
	}
	//!

}
