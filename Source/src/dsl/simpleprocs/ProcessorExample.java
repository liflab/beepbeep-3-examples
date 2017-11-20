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
package dsl.simpleprocs;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.BnfParser.ParseException;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionException;
import dsl.arithmetic.ArithmeticBuilder;

public class ProcessorExample
{
	public static void main(String[] args) throws InvalidGrammarException, ParseException, FunctionException
	{
		ArithmeticBuilder builder = new ArithmeticBuilder();
		Function f = builder.parse("(3) + (4)");
		Object[] out = new Object[1];
		f.evaluate(null, out);
		System.out.println(out[0]); // = 7
	}
}
