package concurrency;

import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.concurrency.NonBlockingPush;
import ca.uqac.lif.cep.concurrency.ParallelWindow;
import ca.uqac.lif.cep.concurrency.Stopwatch;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.BlackHole;

public class WindowParallel 
{
	public static void main(String[] args) throws InterruptedException
	{
		long upper_bound = 200;
		int width = 50;
		GroupProcessor fib_sum = new GroupProcessor(1, 1);
		{
			FibonacciProcessor fib = new FibonacciProcessor();
			Cumulate sum = new Cumulate(new CumulativeFunction<BigInteger>(BigIntegers.addition));
			Connector.connect(fib, sum);
			fib_sum.addProcessors(fib, sum);
			fib_sum.associateInput(0, fib, 0);
			fib_sum.associateOutput(0, sum, 0);
		}
		//SlowPassthrough fib_sum = new SlowPassthrough(1, 200);
		ExecutorService service = Executors.newCachedThreadPool();
		NonBlockingPush nbp = new NonBlockingPush(fib_sum, service);
		ParallelWindow win = new ParallelWindow(nbp, width);
		BlackHole print = new BlackHole();
		//Print print = new Print();
		Connector.connect(win, print);
		Pushable p = win.getPushableInput();
		Stopwatch sw = new Stopwatch();
		sw.start();
		for (int i = 2; i < upper_bound; i++)
		{
			p.push(i);
		}
		System.out.println("All numbers have been pushed in " + sw.elapsed() + " ms");
		service.shutdown();
		service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		sw.stop();
		System.out.println("All windows have been computed in " + sw.elapsed() + " ms");
	}
}
