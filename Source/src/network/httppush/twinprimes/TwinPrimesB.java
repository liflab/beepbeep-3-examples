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

import network.httppush.twinprimes.BigIntegerFunctions.IsPrime;
import network.httppush.twinprimes.BigIntegerFunctions.StringToBigInteger;
import util.UtilityMethods;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.LEFT;
import static ca.uqac.lif.cep.Connector.RIGHT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.cli.Print;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.http.HttpDownstreamGateway;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.jerrydog.RequestCallback.Method;

/**
 * The code for Machine B in the twin prime example.
 * @author Sylvain Hallé
 */
public class TwinPrimesB 
{
	public static void main(String[] args) throws ConnectorException, ProcessorException
	{
		/* First, we create an HttpDownstreamGateway to receive strings
		 * from Machine A. */
		HttpDownstreamGateway dn_gateway = new HttpDownstreamGateway(12312, "/bigprime", Method.POST);
		
		/* The next step is to convert the string received from the gateway
		 * back into a BigInteger. */
		FunctionProcessor string_to_bigint = new FunctionProcessor(StringToBigInteger.instance);
		Connector.connect(dn_gateway, string_to_bigint);
		
		/* We then increment this number by 2. We do this with a special
		 * function. */
		FunctionProcessor big_int_plus_2 = new FunctionProcessor(new BigIntegerFunctions.IncrementBigInteger(new BigInteger("2")));
		Connector.connect(string_to_bigint, big_int_plus_2);
		
		/* Fork the output */
		Fork fork = new Fork(2);
		Connector.connect(big_int_plus_2, fork);
		
		/* Primality check for n+2... */
		FunctionProcessor is_prime = new FunctionProcessor(IsPrime.instance);
		Connector.connect(fork, LEFT, is_prime, INPUT);
		
		/* Join both forks into a filter. What comes out is a stream of numbers 
		 * of the form n+2, such that both n and n+2 are prime. */
		Filter filter = new Filter();
		Connector.connect(fork, RIGHT, filter, LEFT);
		Connector.connect(is_prime, OUTPUT, filter, RIGHT);
		
		/* Print n+2 */
		Print print = new Print();
		Connector.connect(filter, print);
		
		/* All set! Start the gateway so it can listen to requests from
		 * Machine A. */
		dn_gateway.start();
		
		/* Loop indefinitely */
		System.out.println("Machine B listening for requests. Every number displayed below");
		System.out.println("is the second of a twin prime pair. Press Ctrl+C to end.");
		while (true)
		{
			UtilityMethods.pause(10000);
		}
	}
}
