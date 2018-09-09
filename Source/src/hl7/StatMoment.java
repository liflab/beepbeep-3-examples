package hl7;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Numbers;

public class StatMoment extends GroupProcessor
{
  public StatMoment(int order)
  {
    super(1, 1);
    Fork f = new Fork(2);
    ApplyFunction exp = new ApplyFunction(new FunctionTree(
        Numbers.power, 
        StreamVariable.X, 
        new Constant(order)));
    Connector.connect(f, 0, exp, 0);
    Cumulate sum_p = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(exp, sum_p);
    TurnInto one = new TurnInto(1);
    Connector.connect(f, 1, one, 0);
    Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(one, sum_one);
    ApplyFunction div = new ApplyFunction(Numbers.division);
    Connector.connect(sum_p, 0, div, 0);
    Connector.connect(sum_one, 0, div, 1);
    addProcessors(f, exp, sum_p, one, sum_one, div);
    associateInput(0, f, 0);
    associateOutput(0, div, 0);
  }
}
