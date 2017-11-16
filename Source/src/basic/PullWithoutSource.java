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

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.Passthrough;

/**
 * What happens when trying to pull events on a processor connected to
 * no input. The chain of processors in this example is very simple, it
 * is composed of a single {@link ca.uqac.lif.cep.tmf.Passthrough Passthrough}
 * processor:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/Passthrough.png" alt="Processor chain">
 * <p>
 * When attempting to pull an event into the input of this processor, the
 * passthrough will attempt to pull it, in turn, from its input. The
 * problem lies in the fact that <em>nothing</em> is connect to the input.
 * In BeepBeep, every input stream of every processor
 * <em>must</em> be connected to something. The only way to close a chain is
 * by using a {@link ca.uqac.lif.cep.tmf.Source Source}
 * (a processor with zero input stream).  
 * Thus, the expected output of this program is this:
 * <pre>
 * Exception in thread "main" ca.uqac.lif.cep.Pullable$PullableException:
 * Input 0 of this processor is connected to nothing
 *	at ca.uqac.lif.cep.UniformProcessor$OutputPullable.hasNext(UniformProcessor.java:303)
 *	at ca.uqac.lif.cep.UniformProcessor$OutputPullable.pull(UniformProcessor.java:263)
 *	at basic.PullWithoutSource.main(PullWithoutSource.java:62)
 * </pre>
 * <p>
 * The opposite also happens when trying to pull events from a processor
 * connected to no input. See {@link PushWithoutSink}.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class PullWithoutSource
{
	public static void main(String[] args) 
	{
		/* Create a Passthrough processor */
		Passthrough passthrough = new Passthrough();
		/* Get a reference to the Pullable for its input stream */
		Pullable p = passthrough.getPullableOutput();
		/* Try to pull an event. This will throw an exception and stop
		 * the program. */
		p.pull();
	}
}
