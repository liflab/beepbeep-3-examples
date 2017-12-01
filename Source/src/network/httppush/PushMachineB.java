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
package network.httppush;

import java.util.Scanner;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.http.HttpDownstreamGateway;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.serialization.JsonDeserializeString;
import ca.uqac.lif.jerrydog.RequestCallback.Method;

/**
 * This is the same example as {@link PushLocal}, but with the
 * "Machine A" and "Machine B" parts of the chain split into two independent
 * programs, with some user interaction added. You are advised to first
 * go through the {@link PushLocal} example.
 * This file corresponds to Machine B.
 * <p>
 * You can actually run this example on two physical machines if you
 * wish. Simply start {@link PushMachineA} and {@link PushMachineB} on
 * two computers, and use the IP address of Machine B in the URL you give
 * to Machine A.
 * 
 * @author Sylvain Hallé
 */
public class PushMachineB 
{
	public static void main(String[] args) throws ProcessorException, InterruptedException
	{
		// Ask a few infos about the remote end of the process
		System.out.println("Hello, I am Machine B (downstream).");
		System.out.print("Enter the port I should listen to: ");
		Scanner sc = new Scanner(System.in);
		int port = sc.nextInt();

		// Instantiate and pipe the processors 
		HttpDownstreamGateway dn_gateway = new HttpDownstreamGateway(port, "/push", Method.POST);
		FunctionProcessor deserialize = new FunctionProcessor(new JsonDeserializeString<CompoundObject>(CompoundObject.class));
		Print print = new Print();
		Connector.connect(dn_gateway, deserialize);
		Connector.connect(deserialize, print);

		// Start the gateways so it can listen to requests
		dn_gateway.start();

		// Push events on one end
		System.out.println("The received objects will be displayed below. Press q to quit.");
		for (;;)
		{
			String line = sc.nextLine();
			if (line.startsWith("q") || line.startsWith("Q"))
				break;
		}
		// Stop the gateway
		dn_gateway.stop();
		sc.close();
	}

	/**
	 * A dummy object used to test serialization
	 */
	protected static class CompoundObject
	{
		int a;
		String b;
		CompoundObject c;

		protected CompoundObject()
		{
			super();
		}

		public CompoundObject(int a, String b, CompoundObject c)
		{
			super();
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof CompoundObject))
			{
				return false;
			}
			CompoundObject co = (CompoundObject) o;
			if (this.a != co.a || this.b.compareTo(co.b) != 0)
			{
				return false;
			}
			if ((this.c == null && co.c == null) || (this.c != null && co.c != null && this.c.equals(co.c)))
			{
				return true;
			}
			return false;
		}

		@Override
		public String toString()
		{
			return "a = " + a + ", b = " + b + ", c = (" + c + ")";
		}
	}

}
