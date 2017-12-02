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
package diagnose;

import java.math.BigInteger;

import network.httppush.twinprimes.BigIntegerFunctions.BigIntegerAdd;
import network.httppush.twinprimes.BigIntegerFunctions.IsPrime;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.diagnostics.Derivation;
import ca.uqac.lif.cep.diagnostics.ThroughputMeter;
import ca.uqac.lif.cep.diagnostics.WindowConsole;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.BlackHole;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Measure the number of events per second at some point in a
 * processor chain.
 * @author Sylvain Hallé
 */
public class MeasureThroughput 
{

	public static void main(String[] args) throws ProcessorException 
	{
		/*
		 * We setup a counter that generates an increasing sequence of big
		 * integers, and which checks if each of the is prime. We don't care
		 * about the result for the purpose of this example, so we send it
		 * into a black hole.
		 */
		QueueSource source = new QueueSource();
		source.addEvent(new BigInteger("2"));
		CumulativeProcessor counter = new CumulativeProcessor(new CumulativeFunction<BigInteger>(BigIntegerAdd.instance));
		Connector.connect(source, counter);
		ApplyFunction prime_check = new ApplyFunction(IsPrime.instance);
		Connector.connect(counter, prime_check);
		BlackHole sink = new BlackHole();
		Connector.connect(prime_check, sink);
		
		/* Let's perform some diagnostics on this chain of processors. We
		 * would like to see the 1-second throughput inside the pipe between
		 * the prime_check and sink processors. The first step is to create
		 * a throughput meter. We instruct it to display its values in a window
		 * console, and to refresh its values every second. */
		ThroughputMeter meter = new ThroughputMeter(new WindowConsole("Throughput"), 1000);
		
		/* We now install the meter between prime_check and sink. The nice
		 * part about the derivation is that it inserts itself automatically
		 * between two processors using the reconnect() method. If you want to
		 * stop using the derivation, just comment out the call to reconnect;
		 * you don't need to re-pipe the remaining processors. */
		Derivation derivation = new Derivation(meter);
		derivation.reconnect(prime_check, sink);
		
		/* Start the derivation and repeatedly push larger and larger numbers
		 * into the processor chain. */
		derivation.start();
		while (true)
		{
			source.push();
		}
	}

}
