package concurrency;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.concurrency.PushPipeline;
import ca.uqac.lif.cep.tmf.QueueSink;

public class FibonacciParallel 
{
	public static void main(String[] args) throws InterruptedException
	{
		int up_to = 1000;
		ExecutorService service = Executors.newCachedThreadPool();
		FibonacciProcessor fp = new FibonacciProcessor();
		PushPipeline pp = new PushPipeline(fp, service);
		QueueSink bh = new QueueSink();
		Connector.connect(pp, bh);
		Pushable p = pp.getPushableInput();
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
		service.shutdown();
	}	
}
