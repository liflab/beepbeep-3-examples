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

import util.UtilityMethods;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.SingleProcessor;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Pipe processors together using the {@link ca.uqac.lif.cep.Connector Connector}
 * object. The chain of processors in this example can be represented
 * graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/PipingUnary.png" alt="Processor graph">
 * @see PipingBinary
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class PipingUnary
{
	public static void main (String[] args)
	{
		/* Create a source of arbitrary numbers. */
		QueueSource source = new QueueSource();
		source.setEvents(new Integer[]{1, 2, 3, 4, 5, 6});
		
		/* Create an instance of the Doubler processor (which is defined just
		 * below in this file. */
		Doubler doubler = new Doubler();
		
		/* We now want to connect the output of source to the input of doubler.
		 * This connection is done by using static method connect() of the
		 * Connector object. Here, source has only one output stream, and
		 * doubler has only one input stream. Therefore, we can simply call
		 * connect() by giving the "upstream" processor first, and the
		 * "downstream" processor second. The Connector will know that it needs
		 * to connect the (only) output of source to the (only) input of
		 * doubler. */
		Connector.connect(source, doubler);
		
		/* Let us now print what we receive by pulling on the output of
		 * doubler. */
		Pullable p = doubler.getPullableOutput();
		for (int i = 0; i < 8; i++)
		{
			int x = (Integer) p.pull();
			System.out.println("The event is: " + x);
			// Sleep a little so you have time to read
			UtilityMethods.pause(1000);
		}
	}

	/**
	 * A processor that doubles every number it is given. What this
	 * processor does is not really important for our example.
	 */
	public static class Doubler extends SingleProcessor
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3433222467260279668L;

		public Doubler()
		{
			super(1, 1);
		}

		@Override
		protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
		{
			outputs.add(wrapObject(2 * ((Number) inputs[0]).intValue()));
			return true;
		}

		@Override
		public Processor duplicate()
		{
			return this;
		}
	}
}
