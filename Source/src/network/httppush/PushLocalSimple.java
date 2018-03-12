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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.http.HttpDownstreamGateway;
import ca.uqac.lif.cep.http.HttpUpstreamGateway;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.jerrydog.RequestCallback.Method;

/**
 * In this example, Machine A and Machine B are actually the same host; they
 * just listen to different TCP ports on the same computer. Read this program
 * first before looking that the other files in this package.
 * <p>
 * <img
 *   src="{@docRoot}/doc-files/network/httppush/PushLocalSimple.png"
 *   alt="Processor graph">
 * @author Sylvain Hallé
 *
 */
public class PushLocalSimple 
{
	public static void main(String[] args) throws ProcessorException, InterruptedException
	{
		///
		HttpUpstreamGateway up_gateway = new HttpUpstreamGateway("http://localhost:12144/push");
		
		/* We now move on to Machine B, which is responsible for receiving character
		 * strings and converting them back into objects. This is the mirror process of
		 * what was just done. So, the first step is to create an
		 * {@link HttpDownstreamGateway}. The gateway is instructed to listen to incoming
		 * requests on port 12144, to respond to requests made at the page "/push", and
		 * send through an HTTP <code>POST</code> request. */
		HttpDownstreamGateway dn_gateway = new HttpDownstreamGateway(12144, "/push", Method.POST);
		
		/* Just so that we can see something, we plug a {@link Print} processor at
		 * the end; it will print to the standard output whatever object it receives
		 * from upstream. */
		Print print = new Print();
		Connector.connect(dn_gateway, print);

		/* Since the gateways are actually mini-web servers, the servers need to
		 * be launched so that they can actually communicate. This is done by
		 * calling the {@link Processor#start() start()} method on both
		 * processors. Look out, as there can be only one server on a machine
		 * listening to a given TCP port; if you have another instance of this
		 * program already running, the call to start will throw an Exception. */
		up_gateway.start();
		dn_gateway.start();

		/* We are now ready to push events and see what happens. First, we get
		 * a handle on the {@link Pushable} of the very first processor of the
		 * chain, <code>serialize</code> (which resides on Machine A). */
		Pushable p = up_gateway.getPushableInput();
		
		/* Let's push some dummy object. After the call to push, the standard
		 * output should display the contents of that object. */
		p.push("foo");
		
		/* Sleep a little so you have time to look at the console... */
		Thread.sleep(1000);
		
		/* Let's push again. You know the drill. */
		p.push("bar");
		
		/* Once everything is done, we have to stop the servers to free the
		 * TCP ports on your machine. This is done by calling
		 * {@link Processor#stop() stop()} on both processors. */
		up_gateway.stop();
		dn_gateway.stop();
		
		/* That's all folks! */
		///
	}
}
