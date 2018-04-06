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
package basic;

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Pipe processors together using the {@link ca.uqac.lif.cep.Connector Connector}
 * object.
 * The chain of processors in this example can be represented
 * graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/PipingBinary.png" alt="Processor graph">
 * @see PipingUnary
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class PipingBinary
{
	public static void main (String[] args)
	{
		///
		QueueSource source1 = new QueueSource();
		source1.setEvents(2, 7, 1, 8, 3);
		QueueSource source2 = new QueueSource();
		source2.setEvents(3, 1, 4, 1, 6);
		Adder add = new Adder();
		Connector.connect(source1, 0, add, 0);
		Connector.connect(source2, 0, add, 1);
		Pullable p = add.getPullableOutput();
		for (int i = 0; i < 5; i++)
		{
			float x = (Float) p.pull();
			System.out.println("The event is: " + x);
		}
		///
	}
	
	/**
	 * A simple processor that adds two numbers. It is used in examples
	 * throughout this section.
	 */
	public static class Adder extends SingleProcessor
	{
		public Adder()
		{
			super(2, 1);
		}

		@Override
		public Adder duplicate(boolean with_state)
		{
			return new Adder();
		}

		@Override
		protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
		{
			outputs.add(new Object[]{((Integer) inputs[0]) + ((Integer) inputs[1])});
			return true;
		}
	}
}
