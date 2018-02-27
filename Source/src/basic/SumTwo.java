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
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Use the {@link ca.uqac.lif.cep.tmf.Trim Trim} and
 * {@link ca.uqac.lif.cep.tmf.Fork Fork} processors to compute the sum of
 * two successive events. The chain of processors in this example can be
 * represented graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/SumTwo.png" alt="Processor graph">
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class SumTwo
{
	public static void main (String[] args)
	{
		/// Create a source of arbitrary numbers
		QueueSource source = new QueueSource().setEvents(1, 2, 3, 4, 5, 6);
		
		// Fork the stream in two and connect it to the source
		Fork fork = new Fork(2);
		Connector.connect(source, fork);
		
		// Create an addition processor. Connect its first input to the
		// first output of the fork.
		ApplyFunction add = new ApplyFunction(Numbers.addition);
		Connector.connect(fork, 0, add, 0);
		
		/* Create an instance of the Trim processor. This processor is
		 * instructed to discard the first 1 events it receives. Then
		 * connect this to the second output of the fork, and to the
		 * second input of add. */
		Trim trim = new Trim(1);
		Connector.connect(fork, 1, trim, 0);
		Connector.connect(trim, 0, add, 1);
		
		/* Let us now print what we receive by pulling on the output of
		 * add. */
		Pullable p = add.getPullableOutput();
		for (int i = 0; i < 6; i++)
		{
			float x = (Float) p.pull();
			System.out.println("The event is: " + x);
		}
		///
	}
}
