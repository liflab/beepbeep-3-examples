/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package loops;

import static ca.uqac.lif.cep.Connector.connect;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IfThenElse;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Freeze;
import ca.uqac.lif.cep.tmf.Insert;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Numbers;

/**
 * A pipeline whose output behavior takes advantage of the
 * <a href="https://en.wikipedia.org/wiki/Collatz_conjecture">Collatz
 * conjecture</a>.
 * <p>
 * The pipeline receives a stream of arbitrary symbols. The position
 * of the first "a" in the input stream (i.e. the number of events received
 * before observing it) serves as the starting point of a sequence of numbers
 * following the recurrence relation of the Collatz conjecture (see
 * {@link Collatz}). From this point on, the pipeline produces a "b" for each
 * incoming event, unless the current term of the sequence is 1.
 * <p>
 * For example, given the input bba(*)<sup>7</sup> (the * indicates that the
 * actual value of the input event is irrelevant), the pipeline produces the
 * output
 * The table below gives an example of a possible sequence of inputs and
 * outputs:
 * <p>
 * <table border="1">
 * <tr><th></th>      <th>0</th><th>1</th><th>2</th><th>3</th><th>4</th><th>5</th><th>6</th><th>7</th><th>8</th><th>9</th><th>10</th></tr>
 * <tr><th>Input</th> <td>-</td><td>b</td><td>b</td><td>a</td><td>*</td><td>*</td><td>*</td><td>*</td><td>*</td><td>*</td><td>*</td></tr>
 * <tr><th>Output</th><td>b</td><td>-</td><td>-</td><td>b</td><td>b</td><td>b</td><td>b</td><td>a</td><td>b</td><td>b</td><td>b</td></tr>
 * </table>
 * <p>
 * The interest of this pipeline is that the fact that it eventually produces
 * an "a" for any input that contains an "a" depends on the fact that Collatz
 * conjecture is true. Moreover, in such a case the number of output events
 * after which this "a" occurs is finite for any input, but apparently not
 * bounded. 
 * 
 * @see Collatz
 */
public class CollatzComplex
{
  public static void main(String[] args)
  {
    /* Set the position of the first "a" in the input stream by changing
     * the argument of the constructor. */ 
    QueueSource source = new ASource(3).setEvents("b", "b", "a", "b", "b", "b", "b").loop(false);
    
    
    Fork f0 = new Fork();
    connect(source, f0);
    ApplyFunction eq_a = new ApplyFunction(new FunctionTree(Equals.instance, StreamVariable.X, new Constant("a")));
    connect(f0, 0, eq_a, 0);
    Cumulate or = new Cumulate(Booleans.or);
    connect(eq_a, or);
    TurnInto one1 = new TurnInto(1);
    connect(f0, 1, one1, 0);
    Cumulate count1 = new Cumulate(Numbers.addition);
    connect(one1, count1);
    Filter filter = new Filter();
    connect(or, 0, filter, 1);
    connect(count1, 0, filter, 0);
    Freeze fr = new Freeze();
    connect(filter, fr);
    Fork f1 = new Fork();
    connect(fr, f1);
    TurnInto one = new TurnInto(1);
    connect(f1, 0, one, 0);
    Cumulate sum = new Cumulate(Numbers.addition);
    connect(one, sum);
    ApplyFunction leq = new ApplyFunction(new FunctionTree(Numbers.isLessOrEqual, StreamVariable.X, new Constant(1)));
    connect(sum, leq);
    ApplyFunction ite = new ApplyFunction(IfThenElse.instance);
    connect(leq, 0, ite, 0);
    connect(f1, 1, ite, 1);
    Insert ins = new Insert(1, 0);
    connect(ite, ins);
    ApplyFunction term = new ApplyFunction(new Collatz.TermFunction());
    connect(ins, term);
    Fork f2 = new Fork();
    connect(term, f2);
    connect(f2, 1, ite, 2);
    Fork f3 = new Fork(3);
    connect(f2, 0, f3, 0);
    ApplyFunction eq_1 = new ApplyFunction(new FunctionTree(Equals.instance, StreamVariable.X, new Constant(1)));
    connect(f3, 0, eq_1, 0);
    TurnInto in_b = new TurnInto("b");
    connect(f3, 1, in_b, 0);
    TurnInto in_a = new TurnInto("a");
    connect(f3, 2, in_a, 0);
    ApplyFunction ite2 = new ApplyFunction(IfThenElse.instance);
    connect(eq_1, 0, ite2, 0);
    connect(in_b, 0, ite2, 2);
    connect(in_a, 0, ite2, 1);
    
    /* Pull and print events until an "a" is received. */
    Pullable p = ite2.getPullableOutput();
    for (int i = 0; i < 7 && p.hasNext(); i++)
    {
      Object o = p.pull();
      System.out.println(o);
      if (o == "a")
      {
        System.out.println(i);
        break;
      }
    }
  }
  
  protected static class ASource extends QueueSource
  {
    public ASource(int position)
    {
      super();
      for (int i = 0; i < position - 1; i++)
      {
        addEvent("b");
      }
      addEvent("a");
      loop(true);
    }
    /*
    public boolean compute(Object[] inputs, Queue<Object[]> outputs)
    {
    	boolean b = super.compute(inputs, outputs);
    	System.out.println("Q:" + outputs.poll()[0]);
    	return b;
    }*/
  }
}
