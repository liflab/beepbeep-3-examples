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
 * Similar to {@link SimpleMooreMachine}, but with a bit of code written
 * to avoid repetitions.
 * 
 * @see SimpleMooreMachine#main(String[])
 * @author Sylvain Hallé
 *
 */
public class SimpleMooreMachineCompact 
{
	public static void main(String[] args)
	{
		/* Define symbolic constants for the three states of the
		 * Moore machine. It is recommended that the actual numbers for
		 * each state form a contiguous interval of integers starting
		 * at 0. */
		final int UNSAFE = 0, SAFE = 1, ERROR = 2;
		
		/* Create an empty Moore machine. */
		MooreMachine machine = new MooreMachine(1, 1);
		
		/* Let us now define the transitions for this machine. This is just
		 * a tedious enumeration of all the arrows that are present in a
		 * graphical representation of the FSM. */
		addTransition(machine, UNSAFE, "hasnext", SAFE);
		addTransition(machine, UNSAFE, "next", ERROR);
		addTransition(machine, SAFE, "hasnext", SAFE);
		addTransition(machine, SAFE, "next", UNSAFE);
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
	
	/**
	 * Adds a new transition to a Moore machine. The condition on this
	 * transition is an equality check between a String event and a predefined
	 * label.
	 * @param m The machine to add the transition to
	 * @param source The source state
	 * @param label The label to compare the event to
	 * @param destination The destination state
	 */
	protected static void addTransition(MooreMachine m, int source, String label, int destination)
	{
		m.addTransition(source, new FunctionTransition(new FunctionTree(Equals.instance, new StreamVariable(), new Constant(label)), destination));
	}

}
