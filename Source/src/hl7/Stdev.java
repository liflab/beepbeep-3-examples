package hl7;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Numbers;

public class Stdev extends GroupProcessor
{
  public Stdev()
  {
    super(1, 1);
    Fork f = new Fork(2);
    StatMoment e2x = new StatMoment(2);
    Connector.connect(f, 0, e2x, 0);
    StatMoment ex = new StatMoment(1);
    Connector.connect(f, 1, ex, 0);
    ApplyFunction pow2 = new ApplyFunction(new FunctionTree(Numbers.power, StreamVariable.X, new Constant(2)));
    Connector.connect(ex, pow2);
    ApplyFunction sub = new ApplyFunction(Numbers.subtraction);
    Connector.connect(e2x, 0, sub, 0);
    Connector.connect(pow2, 0, sub, 1);
    associateInput(0, f, 0);
    associateOutput(0, sub, 0);
    addProcessors(f, e2x, ex, pow2, sub);
  }
}
