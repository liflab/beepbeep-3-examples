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
package basic;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Trim;
import functions.FunctionUsage.IntegerDivision;

/**
 * Encapsulate a chain of processors into a
 * {@link ca.uqac.lif.cep.Group Group}, with an output arity of 2.
 * The chain of processors in this example can be
 * represented graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/GroupBinary.png" alt="Processor graph">
 * @see SumTwo
 * @author Sylvain Hallé
 * @difficulty Easy 
 */
public class GroupBinary 
{
	public static void main(String[] args)
	{
		// Create a source of arbitrary numbers
		QueueSource source = new QueueSource().setEvents(1, 2, 3, 4, 5, 6);

		// Fork the stream in two and connect it to the source
		GroupProcessor group = new GroupProcessor(1, 2);
		{
			///
			Fork fork = new Fork(2);
			ApplyFunction div = new ApplyFunction(IntegerDivision.instance);
			Connector.connect(fork, 0, div, 0);
			Trim trim = new Trim(1);
			Connector.connect(fork, 1, trim, 0);
			Connector.connect(trim, 0, div, 1);
			group.addProcessors(fork, trim, div);
			group.associateInput(0, fork, 0);
			group.associateOutput(0, div, 0);
			group.associateOutput(1, div, 1);
			///
		}
		
		// Connect the source to the group
		Connector.connect(source, group);

		/* Let us now print what we receive by pulling on the output of
		 * group. */
		Pullable p0 = group.getPullableOutput(1);
		Pullable p1 = group.getPullableOutput(1);
		for (int i = 0; i < 6; i++)
		{
			int q = (Integer) p0.pull();
			int r = (Integer) p1.pull();
			System.out.println(q + " remainder " + r);
		}
		///
	}
}
