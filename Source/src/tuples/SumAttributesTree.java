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
package tuples;

import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.tuples.FetchAttribute;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Compute the sum of two tuple attributes using the 
 * {@link ca.uqac.lif.cep.tuples.GetAttribute GetAttribute} function.
 * This programs reads the file file1.csv, and computes the sum of
 * columns A and B for each line.
 * This example is similar to {@link SumAttributes}, but collapses all
 * functions into a single function tree. This makes the resulting
 * processor chain much simpler.
 * Graphically, this chain of processor can be described as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/tuples/SumAttributesTree.png" alt="Processor graph">
 * <p>
 * The output of this program is:
 * <pre>
 * 5.0
 * 6.0
 * 5.0
 * 9.0
 * 9.0
 * </pre>
 * @author Sylvain Hallé
 * @see SumAttributes
 * @difficulty Easy
 */
public class SumAttributesTree
{
	public static void main(String[] args) 
	{
		///
		InputStream is = SumAttributesTree.class.getResourceAsStream("file1.csv");
		ReadLines reader = new ReadLines(is);
		TupleFeeder tuples = new TupleFeeder();
		Connector.connect(reader, tuples);
		ApplyFunction sum = new ApplyFunction(new FunctionTree(Numbers.addition,
				new FunctionTree(Numbers.numberCast, 
						new FunctionTree(new FetchAttribute("A"), StreamVariable.X)),
				new FunctionTree(Numbers.numberCast, 
						new FunctionTree(new FetchAttribute("B"), StreamVariable.X))));
		Connector.connect(tuples, sum);
		Pullable p = sum.getPullableOutput();
		while (p.hasNext())
		{
			System.out.println(p.next());
		}
		///
	}
}
