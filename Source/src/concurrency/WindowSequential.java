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

import java.math.BigInteger;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.concurrency.ParallelWindow;
import ca.uqac.lif.cep.concurrency.Stopwatch;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.BlackHole;

/**
 * Computes the sum of a sliding window of successive Fibonacci numbers.
 * The goal of this program is to compare its performance with that of a
 * similar one called {@link WindowSequential}. Both perform the same
 * computation. However, in the present program, the computation of the window
 * processor is done *sequentially*. That is, upon receiving an event,
 * the window processor pushes it to each window one after the other in the
 * current thread.
 * The output of the processor chain is not printed anywhere; rather, the
 * program simply counts the *time* taken to process the inputs.
 * 
 * On a machine with multiple cores, this program should run slower than
 * {@link WindowParallel} for the same values of
 * {@link WindowParallel#WIDTH WIDTH} and
 * {@link WindowParallel#NUM_WINDOWS NUM_WINDOWS}. Note that the performance
 * gain may become apparent only for large values of these two parameters.
 * For a small number of small windows, the overhead of using multiple threads
 * offsets the potential performance gains offered by concurrency.
 * 
 * @see WindowParallel
 */
public class WindowSequential 
{
	public static void main(String[] args) throws InterruptedException
	{
		// Create a group processor. From an input event n, it computes the
		// n-th Fibonacci number and sends it to a cumulative sum processor.
		GroupProcessor fib_sum = new GroupProcessor(1, 1);
		{
			FibonacciProcessor fib = new FibonacciProcessor();
			Cumulate sum = new Cumulate(new CumulativeFunction<BigInteger>(BigIntegers.addition));
			Connector.connect(fib, sum);
			fib_sum.addProcessors(fib, sum);
			fib_sum.associateInput(0, fib, 0);
			fib_sum.associateOutput(0, sum, 0);
		}
		// Give this processor as the argument to a Window processor
		ParallelWindow win = new ParallelWindow(fib_sum, WindowParallel.WIDTH);
		// Connect this window to a black hole. In this example, we are not
		// interested in the actual values produced by the processor chain,
		// only in the *time* taken to compute those values.
		BlackHole print = new BlackHole();
		Connector.connect(win, print);
		// Push NUM_WINDOWS events
		Pushable p = win.getPushableInput();
		Stopwatch sw = new Stopwatch();
		sw.start();
		for (int i = 2; i < WindowParallel.NUM_WINDOWS; i++)
		{
			p.push(i);
		}
		// Wait for these events to be processed and show the running time
		sw.stop();
		System.out.println("All windows have been computed in " + sw.elapsed() + " ms");
	}
}