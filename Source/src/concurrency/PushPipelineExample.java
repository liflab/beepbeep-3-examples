package concurrency;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.concurrency.PushPipeline;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Fork;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PushPipelineExample
{
  public static void main(String[] args)
  {
    Fork fork = new Fork(2);
    Print print1 = new Print();
    print1.setPrefix("Push to fork: ").setSeparator("\n");
    Connector.connect(fork, 0, print1, 0);
    SlowPassthrough st = new SlowPassthrough();
    ExecutorService service = Executors.newCachedThreadPool();
    PushPipeline pp = new PushPipeline(st, service);
    Connector.connect(fork, 1, pp, 0);
    Print print2 = new Print();
    print2.setPrefix("From pipeline: ").setSeparator("\n");
    Connector.connect(pp, print2);
    Pushable p = fork.getPushableInput();
    for (int i = 0; i < 10; i++)
    {
      p.push(i);
    }
  }
}
