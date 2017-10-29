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
package http.push;

import http.CompoundObject;

import java.util.Scanner;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.http.HttpUpstreamGateway;
import ca.uqac.lif.cep.serialization.JsonSerializeString;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * This is the same example as {@link PushLocal}, but with the
 * "Machine A" and "Machine B" parts of the chain split into two independent
 * programs, with some user interaction added. You are advised to first
 * go through the {@link PushLocal} example.
 * This file corresponds to Machine A.
 * <p>
 * You can actually run this example on two physical machines if you
 * wish. Simply start {@link PushMachineA} and {@link PushMachineB} on
 * two computers, and use the IP address of Machine B in the URL you give
 * to Machine A.
 * 
 * @author Sylvain Hallé
 */
public class PushMachineA 
{
	public static void main(String[] args) throws ConnectorException, ProcessorException, InterruptedException
	{
		// Ask a few infos about the remote end of the process
		System.out.println("Hello, I am Machine A (upstream).");
		System.out.print("Enter the URL to push on Machine B: ");
		Scanner sc = new Scanner(System.in);
		String url = sc.nextLine();
		
		// Fill a queue source of dummy compound objects 
		QueueSource source = new QueueSource();
		source.addEvent(new CompoundObject(0, "foo", null));
		source.addEvent(new CompoundObject(0, "foo", new CompoundObject(6, "z", null)));
		
		// Connect this queue to a serializer and an upstream gateway
		FunctionProcessor serialize = new FunctionProcessor(new JsonSerializeString());
		HttpUpstreamGateway up_gateway = new HttpUpstreamGateway(url);
		Connector.connect(source, serialize);
		Connector.connect(serialize, up_gateway);
		
		// Start the gateway
		up_gateway.start();
		
		/* Push events out. Here, we simply cycle through the dummy
		 * events we put in the queue source a few times. A press on Enter
		 * triggers the source to push a new event downstream. */
		for (int i = 0; i < 5; i++)
		{
			System.out.println("Press Enter to send a new object to Machine B. Type q to quit.");
			String line = sc.nextLine();
			if (line.startsWith("q") || line.startsWith("Q"))
				break;
			source.push();
		}
		sc.close();

		// Stop the gateway
		up_gateway.stop();
	}

}
