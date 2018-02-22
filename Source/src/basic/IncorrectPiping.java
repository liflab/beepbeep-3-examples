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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Pipe processors with non-matching event types. Doing this in BeepBeep
 * causes a {@link ConnectorException} to be thrown. This example shows what
 * happens in that case. The chain of processors in this example can be represented
 * graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/IncorrectPiping.png" alt="Processor graph">
 * <p>
 * The expected output of this program should look like this:
 * <pre>
 * Exception in thread "main" ca.uqac.lif.cep.Connector$IncompatibleTypesException:
 * Cannot connect output 0 of ABS to input 0 of ¬: incompatible types
 *	at ca.uqac.lif.cep.Connector.checkForException(Connector.java:357)
 *	at ca.uqac.lif.cep.Connector.connect(Connector.java:148)
 *	at ca.uqac.lif.cep.Connector.connect(Connector.java:279)
 *	at ca.uqac.lif.cep.Connector.connect(Connector.java:251)
 *	at basic.IncorrectPiping.main(IncorrectPiping.java:42)
 * </pre>
 * If we refer to the picture, the exception is raised when attempting to
 * connect the output of the first function processor (turquoise, representing
 * a number) to the input of the second function processor (light blue,
 * representing a Boolean).
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class IncorrectPiping
{
	public static void main(String[] args)
	{
		/// Create a simple source with a single numerical event 
		QueueSource source = new QueueSource();
		source.setEvents(3);
		
		/* Create a processor that computes the absolute value of a number
		 * and connect it to that source. */
		Processor av = new ApplyFunction(Numbers.absoluteValue);
		Connector.connect(source, av);
		
		/* Attempt to connect a processor that computes the negation of a
		 * Boolean value to the av processor defined above. This will cause an
		 * exception to be thrown: the output type of av is Number, while the
		 * input type of neg is Boolean. */
		Processor neg = new ApplyFunction(Booleans.not);
		Connector.connect(av, neg); // Will throw an exception
		
		/* As a result, this line of the program should not be reached, as the
		 * execution will stop with the throwing of a ConnectorException on
		 * the line above. */
		System.out.println("This line will not be reached");
		///
	}
}