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

import ca.uqac.lif.cep.UniformProcessor;

/**
 * Processor that computes the n-th Fibonacci number.
 */
public class FibonacciProcessor extends UniformProcessor
{
	public FibonacciProcessor()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs)
	{
		// Perform some long computation
		int index = (Integer) inputs[0];
		BigInteger i = fib(index);
		outputs[0] = i;
		return true;
	}

	@Override
	public FibonacciProcessor duplicate(boolean with_state)
	{
		return new FibonacciProcessor();
	}

	/**
	 * Computes the n-th Fibonacci number.
	 * The actual result of this computation does not really matter;
	 * here we only use it as a CPU-intensive operation.
	 * @param nth The index of the number
	 * @return The Fibonacci number
	 */
	static BigInteger fib(long nth)
	{
		nth = nth - 1;
		long count = 0;
		BigInteger first = BigInteger.ZERO;
		BigInteger second = BigInteger.ONE;
		BigInteger third = null;
		while(count < nth)
		{
			third = new BigInteger(first.add(second).toString());
			first = new BigInteger(second.toString());
			second = new BigInteger(third.toString());
			count++;
		}
		return third;
	}
}