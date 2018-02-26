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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Use a {@link ca.uqac.lif.cep.functions.FunctionTree FunctionTree}
 * to create a complex function.
 * The chain of processors in this example can be represented
 * graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/functions/FunctionTreeUsage.png" alt="Processor graph">
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class FunctionTreeUsage
{
	public static void main (String[] args)
	{
		///
		QueueSource source1 = new QueueSource().setEvents(2, 7, 1, 8, 3);
		QueueSource source2 = new QueueSource().setEvents(3, 1, 4, 1, 6);
		QueueSource source3 = new QueueSource().setEvents(1, 1, 2, 3, 5);
		FunctionTree tree = new FunctionTree(Numbers.multiplication,
				new FunctionTree(Numbers.addition, 
						StreamVariable.X, StreamVariable.Y),
				StreamVariable.Z);
		ApplyFunction exp = new ApplyFunction(tree);
		Connector.connect(source1, 0, exp, 0);
		Connector.connect(source2, 0, exp, 1);
		Connector.connect(source3, 0, exp, 2);
		///
		Pullable p = exp.getPullableOutput();
		for (int i = 0; i < 5; i++)
		{
			float x = (Float) p.pull();
			System.out.println("The event is: " + x);
		}
	}
}
