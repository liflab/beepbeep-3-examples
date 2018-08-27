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
package basic;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.UniformProcessor;

import static ca.uqac.lif.cep.Connector.INPUT;

import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Fork;
import java.util.ArrayList;
import java.util.List;

/**
 * See the effect of sending a mutable object into a 
 * {@link ca.uqac.lif.cep.tmf.Fork Fork} processor. Graphically, the processors
 * of this example can be drawn as follows:
 * <p>
 * <img 
 *   src="{@docRoot}/doc-files/basic/ForkMutable.png" 
 *   alt="Processor graph">
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class ForkMutable 
{
	public static void main(String[] args) throws InterruptedException
	{
	  /* We create a fork processor that will replicate each input event
     * in three output streams. The "3" passed as an argument to the fork's
     * constructor signifies this. */
    ///
    Fork fork = new Fork(3);
    
    /* We now create three "print" processors. Each simply prints to the
     * standard output whatever event they receive. We ask each of them to
     * append their printed line with a different prefix ("Px") so we can
     * know who is printing what. */
    Print p0 = new Print().setSeparator("\n").setPrefix("P0 ");
    Print p1 = new Print().setSeparator("\n").setPrefix("P1 ");
    Print p2 = new Print().setSeparator("\n").setPrefix("P2 ");
    
    /* We finally connect each of the three outputs streams of the fork
     * (numbered 0, 1 and 2) to the input of each print processor. */
    Processor rf = new RemoveFirst();
    Connector.connect(fork, 0, p0, INPUT);
    Connector.connect(fork, 1, rf, INPUT);
    Connector.connect(rf, p1);
    Connector.connect(fork, 2, p2, INPUT);
    ///
    
    /* Let's now push an event to the input of the fork and see what
     * happens. */
    //*
    Pushable p = fork.getPushableInput();
    List<Number> list = new ArrayList<Number>();
    list.add(3); list.add(1); list.add(4);
    p.push(list);
    //*
    
    /* You should normally see
     * P0 foo
     * P1 foo
     * P2 foo
     * be printed almost instantaneously. This shows that all three print
     * processors received their input event at the "same" time. This is
     * not exactly true: the fork processor pushes the event to each of its
     * outputs in sequence; however, since the time it takes to do so is so
     * short, we can consider this to be instantaneous. */
	}
	
	 /**
   * A processor that removes the first element of the list it receives
   * and returns that list. It does not have a special purpose, except
   * to illustrate the behaviour of Fork in some special cases.
   */
  protected static class RemoveFirst extends UniformProcessor
  {
    public RemoveFirst()
    {
      super(1, 1);
      
    }
    @Override
    protected boolean compute(Object[] inputs, Object[] outputs)
    {
      List<?> list = (List<?>) inputs[0];
      list.remove(0);
      outputs[0] = list;
      return true;
    }

    @Override
    public Processor duplicate(boolean with_state)
    {
      // Not necessary to implement this for the example
      return null;
    }
  }
}
