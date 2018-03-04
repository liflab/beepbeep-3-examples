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

import java.util.Scanner;

import ca.uqac.lif.cep.dsl.GrammarObjectBuilder;
import ca.uqac.lif.cep.functions.Function;

/**
 * Use the {@link ArithmeticBuilder} as an interactive calculator.
 * @author Sylvain Hallé
 * @difficulty Medium
 * @see ArithmeticBuilder
 */
public class Calculator extends GrammarObjectBuilder<Function>
{
	public static void main(String[] args) throws BuildException
	{
		///
		Scanner scanner = new Scanner(System.in);
		ArithmeticBuilder builder = new ArithmeticBuilder();
		while (true)
		{
			System.out.print("? ");
			String line = scanner.nextLine();
			if (line.equalsIgnoreCase("q"))
				break;
			Function f = builder.build(line);
			Object[] value = new Object[1];
			f.evaluate(new Object[]{}, value);
			System.out.println(value[0]);
		}
		scanner.close();
		///
	}
}
