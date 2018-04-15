package concurrency;

import java.math.BigInteger;
import java.util.concurrent.locks.ReentrantLock;

import ca.uqac.lif.cep.UniformProcessor;

/**
 * Processor that computes the n-th Fibonacci number.
 */
public class FibonacciProcessor extends UniformProcessor
{
	private static ReentrantLock s_callLock = new ReentrantLock();
	private static int s_calls = 0;
	
	public static int getCalls()
	{
		int calls = 0;
		s_callLock.lock();
		calls = s_calls;
		s_callLock.unlock();
		return calls;
	}
	
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
		//System.out.println("FIB : " + index  + " DONE");
		outputs[0] = i;
		s_callLock.lock();
		s_calls++;
		s_callLock.unlock();
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
		//System.out.println("FIB : " + nth);
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