package ltl;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.ltl.Eventually;

public class EventuallySimple
{

  public static void main(String[] args)
  {
    ///
    Eventually e = new Eventually();
    Print print = new Print();
    Connector.connect(e, print);
    Pushable p = e.getPushableInput();
    System.out.println("Pushing false");
    p.push(false);
    System.out.println("Pushing false");
    p.push(false);
    System.out.println("Pushing true");
    p.push(true);
    System.out.println("Pushing false");
    p.push(false);
    ///
  }

}
