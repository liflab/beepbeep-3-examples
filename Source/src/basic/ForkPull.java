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
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Use the {@link ca.uqac.lif.cep.tmf.Fork Fork} processor to replicate
 * input events in multiple output streams. Graphically, the processors
 * of this example can be drawn as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/ForkPull.png" alt="Processor graph">
 * <p>
 * The expected output of this program is:
 * <pre>
 * P0 foo
 * P1 foo
 * P2 foo
 * P0 bar
 * P1 bar
 * P2 bar
 * </pre>
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class ForkPull 
{
	public static void main(String[] args) throws InterruptedException
	{
		/// Create a queue source with a few numbers
		QueueSource source = new QueueSource().setEvents(1, 2, 3, 4, 5);
		
		/* We create a fork processor that will replicate each input event
		 * in two output streams. The "2" passed as an argument to the fork's
		 * constructor signifies this. Then we connect the source to the
		 * fork's input pipe. */
		Fork fork = new Fork(2);
		Connector.connect(source, fork);
		
		/* We get Pullables on both outputs of the fork. */
		Pullable p0 = fork.getPullableOutput(0);
		Pullable p1 = fork.getPullableOutput(1);
		
		/* Let's now pull an event from p0. The output is 1. */
		System.out.println("Output from p0: " + p0.pull());
		
		/* Let's now pull an event from p1. Surprisingly, the output is 1.
		 * This can be explained by the fact that each input event in the
		 * fork is replicated to all its output pipes. The fact that we
		 * pulled an event from p0 has no effect on p1, and vice versa. */
		System.out.println("Output from p1: " + p1.pull());
		///
		
		/* The independence between the fork's two outputs is further
		 * illustrated by this sequence of calls. Notice how each pullable
		 * moves through the input stream independently of calls to the
		 * other pullable. */
		//*
		System.out.println("Output from p0: " + p0.pull());
		System.out.println("Output from p0: " + p0.pull());
		System.out.println("Output from p1: " + p1.pull());
		System.out.println("Output from p0: " + p0.pull());
		System.out.println("Output from p1: " + p1.pull());
		//*
	}

}
