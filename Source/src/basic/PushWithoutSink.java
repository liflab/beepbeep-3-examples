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

import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.tmf.Passthrough;

/**
 * What happens when trying to push events on a processor connected to
 * no output. The chain of processors in this example is very simple, it
 * is composed of a single {@link ca.uqac.lif.cep.tmf.Passthrough Passthrough}
 * processor:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/Passthrough.png" alt="Processor chain">
 * <p>
 * When attempting to push an event into the input of this processor, the
 * passthrough will attempt to push it, in turn, through its output. The
 * problem lies in the fact that <em>nothing</em> is connect to the output.
 * In BeepBeep, pushed events cannot just disappear into thin air: they
 * <em>must</em> be collected by some {@link ca.uqac.lif.cep.tmf.Sink Sink}
 * (a processor with zero output streams).  
 * Thus, the expected output of this program is this:
 * <pre>
 * Exception in thread "main" ca.uqac.lif.cep.Pushable$PushableException:
 * Output 0 of this processor is connected to nothing
 *	at ca.uqac.lif.cep.UniformProcessor$InputPushable.push(UniformProcessor.java:178)
 *	at basic.PushWithoutSink.main(PushWithoutSink.java:35)
 * </pre>
 * <p>
 * The opposite also happens when trying to pull events from a processor
 * connected to no input. See {@link PullWithoutSource}.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class PushWithoutSink
{
	public static void main(String[] args) 
	{
		/// Create a Passthrough processor
		Passthrough passthrough = new Passthrough();
		/* Get a reference to the Pushable for its input stream */
		Pushable p = passthrough.getPushableInput();
		/* Try to push an event. This will throw an exception and stop
		 * the program. */
		p.push("foo");
		///
	}
}
