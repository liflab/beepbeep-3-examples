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
package loops;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Insert;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Prints the first terms of the
 * <a href="https://en.wikipedia.org/wiki/Fibonacci_sequence">Fibonacci
 * sequence</a>.
 * <p>
 * The output of this program is (for <i>n</i> = 10 terms):
 * <pre>
 * 2,3,5,8,13,21,34,55,89,144</pre>
 * Note that the two starting terms of the sequence (1 and 1) are not printed.
 * The pipeline corresponding to this program is illustrated by the following
 * diagram:
 * <p>
 * <img src="{@docRoot}/doc-files/loops/Fibonacci.png" alt="Pipeline" />
 */
public class Fibonacci
{
	public static void main(String[] args)
	{
		/* The number of terms to print. */
		int num_terms = 10;
		
		ApplyFunction fib = new ApplyFunction(Numbers.addition);
		Fork f = new Fork(3);
		Connector.connect(fib, f);
		Insert in1 = new Insert(2, 1);
		Connector.connect(f, 0, in1, 0);
		Connector.connect(in1, 0, fib, 0);
		Insert in2 = new Insert(1, 1);
		Connector.connect(f, 2, in2, 0);
		Connector.connect(in2, 0, fib, 1);
		Pump p = new Pump();
		Connector.connect(f, 1, p, 0);
		Print print = new Print();
		Connector.connect(p, print);
		p.turn(num_terms);
	}

}
