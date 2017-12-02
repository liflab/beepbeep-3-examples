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
package network.httppush.twinprimes;

import java.math.BigInteger;

import network.httppush.twinprimes.BigIntegerFunctions.BigIntegerAdd;
import network.httppush.twinprimes.BigIntegerFunctions.BigIntegerToString;
import network.httppush.twinprimes.BigIntegerFunctions.IsPrime;
import util.UtilityMethods;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.LEFT;
import static ca.uqac.lif.cep.Connector.RIGHT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.http.HttpUpstreamGateway;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * The code for Machine A in the twin prime example.
 * @author Sylvain Hallé
 */
public class TwinPrimesA 
{
	public static void main(String[] args) throws ProcessorException
	{
		/* The URL where prime numbers will be pushed downstream. Change
		 * this string to correspond to Machine B's address and port. */ 
		String push_url = "http://localhost:12312/bigprime";
		
		/* The first processor is a source that will push the BigInteger
		 * "2" repeatedly. */
		QueueSource source = new QueueSource();
		source.addEvent(new BigInteger("2"));
		
		/* The second processor is a simple counter. We will feed it with the
		 * BigInteger "2" repeatedly, and it will return the cumulatve sum
		 * of those "2" as its output. Since the start value of BigIntegerAdd
		 * is one, the resulting sequence is made of all odd numbers. */
		CumulativeProcessor counter = new CumulativeProcessor(new CumulativeFunction<BigInteger>(BigIntegerAdd.instance));
		Connector.connect(source, counter);
		
		/* The events output from the counter are duplicated along two paths. */
		Fork fork1 = new Fork(2);
		Connector.connect(counter, fork1);
		
		/* Along the first path, the numbers are checked for primality. */ 
		ApplyFunction prime_check = new ApplyFunction(IsPrime.instance);
		Connector.connect(fork1, LEFT, prime_check, INPUT);
		
		/* Along the second path, we feed a filter and use the primality
		 * verdict as the filtering condition. What comes out of the filter
		 * are only prime numbers. */
		Filter filter = new Filter();
		Connector.connect(fork1, RIGHT, filter, LEFT);
		Connector.connect(prime_check, OUTPUT, filter, RIGHT);
		
		/* We fork the output of the filter, just so that we can
		 * print what comes out of it. */
		Fork fork2 = new Fork(2);
		Connector.connect(filter, fork2);
		Print print = new Print();
		Connector.connect(fork2, LEFT, print, INPUT);
		
		/* We convert BigIntegers to Strings, and push them across the network
		 * to Machine B using the HttpUpstreamGateway. */
		ApplyFunction int_to_string = new ApplyFunction(BigIntegerToString.instance);
		HttpUpstreamGateway up_gateway = new HttpUpstreamGateway(push_url);
		Connector.connect(fork2, RIGHT, int_to_string, INPUT);
		Connector.connect(int_to_string, up_gateway);
		
		/* All set! We are ready to start the gateway, and repeatedly push
		 * primes to Machine B. */
		System.out.println("This is Machine A. Press Enter to start pushing numbers to Machine B.");
		UtilityMethods.readLine();
		System.out.println("Let's go! Pushing prime numbers to " + push_url);
		System.out.println("Press Ctrl+C to stop.");
		up_gateway.start();
		while (true)
		{
			source.push();
			/* Sleep a little to let the requests go through. We deliberately loop
			   slowly so you have time to look at the screen! */
			UtilityMethods.pause(500);
		}
	}
}
