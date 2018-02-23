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
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Numbers;
import util.UtilityMethods;

/**
 * Pipe processors together using the {@link ca.uqac.lif.cep.Connector Connector}
 * object.
 * The chain of processors in this example is almost identical to
 * {@link PipingBinary}, and can be represented graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/PipingBinaryWait.png" alt="Processor graph">
 * <p>
 * The difference is that the first queue source has been replaced by a
 * "slow" queue source, that waits 5 seconds before outputting an event.
 * @see PipingBinary
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class PipingBinaryWait
{
	public static void main (String[] args)
	{
		///
		SlowQueueSource source1 = new SlowQueueSource();
		source1.setEvents(2, 7, 1, 8, 3);
		QueueSource source2 = new QueueSource();
		source2.setEvents(3, 1, 4, 1, 6);
		ApplyFunction add = new ApplyFunction(Numbers.addition);
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
	 * A queue source object that waits 5 seconds before outputting
	 * each event.
	 */
	public static class SlowQueueSource extends QueueSource
	{
		@Override
		public boolean compute(Object[] inputs, Queue<Object[]> outputs)
		{
			UtilityMethods.pause(5000);
			return super.compute(inputs, outputs);
		}
	}
}
