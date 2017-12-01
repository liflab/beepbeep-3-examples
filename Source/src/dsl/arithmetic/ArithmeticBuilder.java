/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

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
package dsl.arithmetic;

import java.util.Stack;

import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.util.Numbers;

public class ArithmeticBuilder extends TopStackParseTreeBuilder<Function>
	{
		@Override
		public String getGrammar()
		{
			return FileHelper.internalFileToString(ArithmeticExample.class, "arithmetic.bnf");
		}
		
		public void doAddition(Stack<Object> stack)
		{
			processBinary(stack, Numbers.addition);
		}

		public void doSubtraction(Stack<Object> stack)
		{
			processBinary(stack, Subtraction.instance);
		}
		
		public void doNumber(Stack<Object> stack)
		{
			String s = (String) stack.pop();
			Integer i = Integer.parseInt(s);
			stack.push(new Constant(i));
		}
		
		protected void processBinary(Stack<Object> stack, Function f)
		{
			stack.pop(); // )
			Function arg_2 = (Function) stack.pop();
			stack.pop(); // (
			stack.pop(); // +
			stack.pop(); // )
			Function arg_1 = (Function) stack.pop();
			stack.pop(); // (
			FunctionTree tree = new FunctionTree(f, arg_1, arg_2);
			stack.push(tree);
		}
	}