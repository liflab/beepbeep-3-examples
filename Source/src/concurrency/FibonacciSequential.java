package concurrency;

import java.util.Queue;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.QueueSink;

public class FibonacciSequential 
{
	public static void main(String[] args) throws InterruptedException
	{
		int up_to = 1000;
		FibonacciProcessor fp = new FibonacciProcessor();
		QueueSink bh = new QueueSink();
		Connector.connect(fp, bh);
		Pushable p = fp.getPushableInput();
		long start = System.currentTimeMillis();
		for (int i = 2; i <= up_to; i++)
		{
			p.push(i);
		}
		Queue<Object> q = bh.getQueue();
		while (q.size() < up_to - 2)
		{
			// Wait
			Thread.sleep(10);
		}
		long stop = System.currentTimeMillis();
		System.out.println("Duration: " + (stop - start));
	}
}
