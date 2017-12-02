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
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.fsm.FunctionTransition;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Equals;

/**
 * Check the proper ordering of next/hasnext strings. In this example, we
 * suppose we get a stream of method calls on an <tt>Iterator</tt> object.
 * The proper use of an iterator stipulates that one should never call method
 * <code>next()</code> before first calling method <code>hasNext()</code>.
 * The correct ordering of these calls can be expressed by a finite-state
 * machine with three states. 
 * <p>
 * On the input stream "hasnext", "next", "hasnext", "hasnext", "next", "next", 
 * the expected output of this program is:
 * <pre>
 * unsafe
 * safe
 * unsafe
 * unsafe
 * safe
 * error
 * error
 * </pre>
 * <p>
 * The basic Moore machine is very generic. The conditions on its transitions
 * can be arbitrary functions on incoming events; as a result, defining a
 * simple state machine can be a little verbose. For a more "compact" way of
 * defining the state machine in this example, see {@link SimpleMooreMachineCompact}.
 * 
 * @see SimpleMooreMachineCompact#main(String[])
 * @author Sylvain Hallé
 *
 */
public class SimpleMooreMachine 
{
	public static void main(String[] args)
	{
		/* Define symbolic constants for the three states of the
		 * Moore machine. It is recommended that the actual numbers for
		 * each state form a contiguous interval of integers starting
		 * at 0. */
		final int UNSAFE = 0, SAFE = 1, ERROR = 2;
		
		/* Create an empty Moore machine. This machine receives one stream of
		 * events as its input, and produces one stream of events as its
		 * output. */
		MooreMachine machine = new MooreMachine(1, 1);
		
		/* Let us now define the transitions for this machine. This is just
		 * a tedious enumeration of all the arrows that are present in a
		 * graphical representation of the FSM. First, in state 0, if the
		 * incoming event is equal to "hasnext", go to state 1. 
		 * 
		 * By default, the first state number that is ever given to the
		 * MooreMachine object is taken as the initial state of that machine.
		 * So here, UNSAFE will be the initial state. There can be only one
		 * initial state. */
		machine.addTransition(UNSAFE, new FunctionTransition(
				new FunctionTree(Equals.instance, new StreamVariable(), new Constant("hasnext")), SAFE));
		/* In state 0, if the incoming event is equal to "next", go to state 2 */
		machine.addTransition(UNSAFE, new FunctionTransition(
				new FunctionTree(Equals.instance, new StreamVariable(), new Constant("next")), ERROR));
		/* In state 1, if the incoming event is equal to "next", go to state 0 */
		machine.addTransition(SAFE, new FunctionTransition(
				new FunctionTree(Equals.instance, new StreamVariable(), new Constant("next")), UNSAFE));
		/* In state 1, if the incoming event is equal to "hasnext", stay in state 1 */
		machine.addTransition(SAFE, new FunctionTransition(
				new FunctionTree(Equals.instance, new StreamVariable(), new Constant("hasnext")), SAFE));
		/* State 2 is a sink, you stay there forever. A possible way to say so
		 * is to define the condition on its only transition as the constant true;
		 * it will fire whatever the incoming event. */
		machine.addTransition(ERROR, new FunctionTransition(
				new Constant(true), ERROR));
		
		/* Since a Moore machine outputs a symbol when jumping into a
		 * new state, we must associate symbols to each of the three states. 
		 * These symbols can be any object; here we use character strings. */
		machine.addSymbol(UNSAFE, "safe").addSymbol(SAFE, "unsafe").addSymbol(ERROR, "error");
		
		/* Done! We can now try our Moore machine on a sequence of events .*/
		QueueSource source = new QueueSource();
		source.setEvents("hasnext", "next", "hasnext", "hasnext", "next", "next");
		Connector.connect(source, machine);
		
		/* Let's pull a few events to see what comes out... */
		Pullable p = machine.getPullableOutput();
		for (int i = 0; i < 7; i++)
		{
			String s = (String) p.pull();
			System.out.println(s);
		}
	}

}
