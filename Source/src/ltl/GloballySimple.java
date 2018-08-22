package ltl;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.ltl.Globally;

public class GloballySimple
{

  public static void main(String[] args)
  {
    ///
    Globally g = new Globally();
    Print print = new Print();
    Connector.connect(g, print);
    Pushable p = g.getPushableInput();
    System.out.println("Pushing true");
    p.push(true);
    System.out.println("Pushing true");
    p.push(true);
    ///
    //*
    System.out.println("Pushing false");
    p.push(false);
    System.out.println("Pushing true");
    p.push(true);
    //*
  }

}
