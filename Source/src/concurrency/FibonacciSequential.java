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
package concurrency;

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.QueueSink;

/**
 * Computes the first <i>N</i> Fibonacci numbers. This is done by pushing
 * the integers 2, 3, &hellip; into a {@link FibonacciProcessor} that computes
 * the 2nd, 3rd, &hellip; Fibonacci number. 
 * <p>
 * On a machine with multiple cores, this program should run slower than
 * {@link FibonacciParallel} for the same value of
 * {@link WindowParallel#UPPER_BOUND UPPER_BOUND}. Note that the performance
 * gain may become apparent only for a large value of this parameter.
 * If only a small number of events are pushed the overhead of using multiple
 * threads offsets the potential performance gains offered by concurrency.
 * 
 * @see FibonacciParallel
 */
public class FibonacciSequential 
{
	public static void main(String[] args) throws InterruptedException
	{
		// Create a new Fibonacci processor
		FibonacciProcessor fp = new FibonacciProcessor();
		// Connect this processor to a sink
		QueueSink bh = new QueueSink();
		Connector.connect(fp, bh);
		// Push integers from 2 to UPPER_BOUND in rapid-fire fashion into
		// this processor.
		Pushable p = fp.getPushableInput();
		long start = System.currentTimeMillis();
		for (int i = 2; i <= FibonacciParallel.UPPER_BOUND; i++)
		{
			p.push(i);
		}
		// Wait for all threads to be done. We do this by repeatedly polling
		// the output queue's size. All numbers are computed when UPPER_BOUND-2
		// numbers are present.
		Queue<Object> q = bh.getQueue();
		while (q.size() < FibonacciParallel.UPPER_BOUND - 2)
		{
			// Wait
			Thread.sleep(10);
		}
		// Print elapsed time
		long stop = System.currentTimeMillis();
		System.out.println("Duration: " + (stop - start));
	}
}
