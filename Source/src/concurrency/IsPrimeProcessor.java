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
import java.util.Queue;

import ca.uqac.lif.cep.SynchronousProcessor;

/**
 * Processor that checks the primality of a big integer.
 * Its goal is not to be efficient, but rather to be CPU-intensive.
 */
public class IsPrimeProcessor extends SynchronousProcessor
{
	public IsPrimeProcessor()
	{
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
	{
		BigInteger divisor = BigInteger.ONE.add(BigInteger.ONE);
		BigInteger number = (BigInteger) inputs[0];
		BigInteger sqrt_number = BigIntegers.sqrt(number);
		@SuppressWarnings("unused")
		boolean prime = true;
		//System.out.println("PRIME : " + number);
		while (divisor.compareTo(sqrt_number) <= 0)
		{
			BigInteger result = number.remainder(divisor);
			if (result.compareTo(BigInteger.ZERO) == 0)
			{
				prime = false;
			}
			divisor = divisor.add(BigInteger.ONE);
		}
		System.out.println("PRIME : " + number + " DONE");
		outputs.add(inputs);
		return true;
	}

	@Override
	public IsPrimeProcessor duplicate(boolean with_state)
	{
		// Don't care
		return this;
	}
}