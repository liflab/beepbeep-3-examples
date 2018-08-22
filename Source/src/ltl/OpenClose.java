package ltl;

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.ApplyFunctionLazy;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.ltl.Eventually;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;

public class OpenClose
{

  public static void main(String[] args)
  {
    ///
    Fork f1 = new Fork(2);
    ApplyFunctionLazy imp = new ApplyFunctionLazy(
        new FunctionTree(Booleans.implies,
            new FunctionTree(Equals.instance, 
                StreamVariable.X, new Constant("open")),
            StreamVariable.Y));
    ApplyFunction close = new ApplyFunction(
        new FunctionTree(Equals.instance,
            StreamVariable.X, new Constant("close")));
    Eventually e = new Eventually();
    Fork f2 = new Fork(2);
    Connector.connect(f1, BOTTOM, f2, INPUT);
    Connector.connect(f2, TOP, imp, TOP);
    Connector.connect(f2, BOTTOM, close, INPUT);
    Connector.connect(close, e);
    Connector.connect(e, OUTPUT, imp, BOTTOM);
    Filter filter = new Filter();
    Connector.connect(f1, TOP, filter, TOP);
    Connector.connect(imp, OUTPUT, filter, BOTTOM);
    Print print = new Print();
    print.setPrefix("Output: ").setSeparator("\n");
    Connector.connect(filter, print);
    Pushable p = f1.getPushableInput();
    System.out.println("Pushing nop");
    p.push("nop");
    System.out.println("Pushing open");
    p.push("open");
    System.out.println("Pushing read");
    p.push("read");
    System.out.println("Pushing close");
    p.push("close");
    ///
  }

}
