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
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IfThenElse;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Insert;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Illustrates the
 * <a href="https://en.wikipedia.org/wiki/Collatz_conjecture">Collatz
 * conjecture</a> by showing the corresponding sequence from a starting
 * number <i>n</i>. The conjecture states that repeatedly applying a
 * specific {@link TermFunction function} to a starting number produces a
 * sequence that always reaches the number 1. 
 * <p>
 * The output of this program is (for <i>n</i> = 17):
 * <pre>
 * 26
 * 13
 * 20
 * 10
 * 5
 * 8
 * 4
 * 2
 * 1</pre>
 * The pipeline corresponding to this program is illustrated by the following
 * diagram:
 * <p>
 * <img src="{@docRoot}/doc-files/loops/Collatz.png" alt="Pipeline" />
 * 
 * @see CollatzComplex
 */
public class Collatz
{
	public static void main(String[] args)
	{
		/* The starting value of the sequence. */
		int n = 3;
		
		ApplyFunction seq = new ApplyFunction(new TermFunction());
		Fork f = new Fork();
		Connector.connect(seq, f);
		Insert in1 = new Insert(1, n);
		Connector.connect(f, 0, in1, 0);
		Connector.connect(in1, 0, seq, 0);
		Pullable p = f.getPullableOutput(1);
		int x = 0;
		while (x != 1 && p.hasNext())
		{
			x = ((Number) p.next()).intValue();
			System.out.println(x);
		}
	}
	
	/**
	 * Evaluates the function that calculates the next term of the Collatz
	 * sequence. The function is defined as <i>f</i>(<i>n</i>) = <i>n</i> / 2 if
	 * <i>n</i> &equiv; 0 (mod 2), and (3<i>n</i>+1) / 2 otherwise.
	 */
	public static class TermFunction extends FunctionTree
	{
		public TermFunction()
		{
			super(IfThenElse.instance,
					Numbers.isEven,
					new FunctionTree(Numbers.division, StreamVariable.X, new Constant(2)),
					new FunctionTree(Numbers.division, new FunctionTree(Numbers.addition, new FunctionTree(Numbers.multiplication, StreamVariable.X, new Constant(3)), new Constant(1)), new Constant(2))
			);
		}
	}

}
