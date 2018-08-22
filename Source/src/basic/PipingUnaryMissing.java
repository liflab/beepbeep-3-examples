/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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

import basic.PipingUnary.Doubler;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Instantiates two processors, but forgets to connect them. This example is
 * identical to {@link PipingUnary}, but without the call to {@code connect}.
 * <p>
 * <img src="{@docRoot}/doc-files/basic/PipingUnaryMissing.png" alt="Processor graph">
 * <p>
 * Notice how a pipe between the source and the Doubler processor is missing.
 * Attempting to call {@code pull} on {@code doubler} will throw an exception.
 * The expected output of the program should look like this:
 * <pre>
 * Exception in thread "main" ca.uqac.lif.cep.Pullable$PullableException: Input 0 of this processor is connected to nothing
	at ca.uqac.lif.cep.SynchronousProcessor$OutputPullable.hasNext(SynchronousProcessor.java:396)
	at ca.uqac.lif.cep.SynchronousProcessor$OutputPullable.pull(SynchronousProcessor.java:354)
	at basic.PipingUnaryMissing.main(PipingUnaryMissing.java:51)
 * </pre>
 * @see PipingUnary
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class PipingUnaryMissing
{
	public static void main (String[] args)
	{
		// Create a source of arbitrary numbers
		QueueSource source = new QueueSource();
		source.setEvents(1, 2, 3, 4, 5, 6);
		
		/* Create an instance of the Doubler processor (which is defined just
		 * below in this file. */
		Doubler doubler = new Doubler();
		
		/* We do NOT connect the two processors and try to call pull
		 * on doubler. This will throw an exception. */
		///
		Pullable p = doubler.getPullableOutput();
		System.out.println("The event is: " + p.pull());
		///
	}
}
