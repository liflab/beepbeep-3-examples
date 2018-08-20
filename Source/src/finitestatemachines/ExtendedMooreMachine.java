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
package finitestatemachines;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.ContextAssignment;
import ca.uqac.lif.cep.functions.ContextVariable;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.fsm.FunctionTransition;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Booleans.And;

/**
 * Create an extended state machine with state variables. This is done by
 * having the state machine manipulate its {@link Context} object.
 * <p>
 * You should first have a look at {@link SimpleMooreMachine} before reading
 * this example.
 * 
 * @author Sylvain Hallé
 *
 */
public class ExtendedMooreMachine 
{
	public static void main(String[] args) 
	{
	  ///
		MooreMachine machine = new MooreMachine(1, 1);
		machine.setContext("c", 0);
		machine.setContext("n", 1);
		///
		{
		  //!
		  FunctionTree guard = new FunctionTree(And.instance, 
          new FunctionTree(Equals.instance, 
              StreamVariable.X, new Constant("hasNext")),
          new FunctionTree(Numbers.isLessThan,
              new ContextVariable("c"), new ContextVariable("n")));
		  //!
		  //*
		  ContextAssignment asg = new ContextAssignment("c",
		      new FunctionTree(Numbers.addition,
		          new ContextVariable("c"), new Constant(1))
		      );
		  //*
		  //%
		  machine.addTransition(0, new FunctionTransition(
	        guard, 0, asg));
		  //%
		  //#
		  machine.addTransition(0, new FunctionTransition(
		      new FunctionTree(And.instance, 
		          new FunctionTree(Equals.instance, 
		              StreamVariable.X, new Constant("next")),
		          new FunctionTree(Equals.instance,
		              new ContextVariable("c"), new ContextVariable("n"))),
		      0,
		      new ContextAssignment("c", new Constant(0)),
		      new ContextAssignment("n",
		          new FunctionTree(Numbers.addition,
		              new ContextVariable("n"), new Constant(1))
		          )
		      ));
		  //#
		  //@
		  machine.addTransition(0, new TransitionOtherwise(1));
		  machine.addTransition(1, new TransitionOtherwise(1));
		  //@
		  //(
		  machine.addSymbol(0, new ContextVariable("c"));
		  //(
		  QueueSource source = new QueueSource();
		  source.setEvents("hasNext", "next", "hasNext", "hasNext", "next", "next", "hasNext");
		  source.loop(false);
		  Connector.connect(source, machine);
		  Pullable p = machine.getPullableOutput();
		  while (p.hasNext())
		  {
		    Object o = p.pull();
		    System.out.println(o);
		  }
		}
	}
}
