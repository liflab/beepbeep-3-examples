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

import network.CompoundObject;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.cli.Print;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.http.HttpDownstreamGateway;
import ca.uqac.lif.cep.http.HttpUpstreamGateway;
import ca.uqac.lif.cep.serialization.JsonDeserializeString;
import ca.uqac.lif.cep.serialization.JsonSerializeString;
import ca.uqac.lif.jerrydog.RequestCallback.Method;

/**
 * In this example, Machine A and Machine B are actually the same host; they
 * just listen to different TCP ports on the same computer. Read this program
 * first before looking that the other files in this package.
 * @author Sylvain Hallé
 *
 */
public class PushLocal 
{
	public static void main(String[] args) throws ConnectorException, ProcessorException, InterruptedException
	{
		/* We first setup a {@link FunctionProcessor} that will execute
		 * the function {@link JsonSerializeString} on each input event. This function
		 * transforms an incoming object into a character string in the JSON format,
		 * through a process called <em>serialization</em>. Under the hood, the Azrael
		 * library takes care of this task. */ 
		FunctionProcessor serialize = new FunctionProcessor(new JsonSerializeString());

		/* This function processor is connected to a {@link HttpUpstreamGateway}.
		 * The gateway is a processor that transmits its received events to another
		 * machine, through HTTP requests and responses. Since the chain operates in
		 * <em>push</em> mode, the gateway will be pushed character strings from
		 * upstream, and will in turn push them to the outside world by sending an HTTP
		 * request at a specific address. Thus, when instantiating the gateway, we must
		 * tell it the URL at which the request will be sent. In this case, the URL
		 * for Machine B is on the same host, on port 12144. The "/push" prefix is the
		 * "page" on Machine B the server will respond to. */
		HttpUpstreamGateway up_gateway = new HttpUpstreamGateway("http://localhost:12144/push");
		
		/* We now move on to Machine B, which is responsible for receiving character
		 * strings and converting them back into objects. This is the mirror process of
		 * what was just done. So, the first step is to create an
		 * {@link HttpDownstreamGateway}. The gateway is instructed to listen to incoming
		 * requests on port 12144, to respond to requests made at the page "/push", and
		 * send through an HTTP <code>POST</code> request. */
		HttpDownstreamGateway dn_gateway = new HttpDownstreamGateway(12144, "/push", Method.POST);
		
		/* Each event output from this processor is a sequence of character strings,
		 * taken from the payload of each HTTP request that is received. Remember
		 * that these strings are in the JSON format, and correspond to the serialized
		 * content of objects. The next step is to transform these strings back into
		 * objects, i.e. to <em>deserialize</em> them. This is done by a
		 * {@link FunctionProcessor} that applies the {@link JsonDeserializeString}
		 * function (again, using the Azrael library in the background). Note that
		 * this function must be given the class of the objects
		 * to be deserialized, so that it knows instances of what kind of objects
		 * to create. */
		FunctionProcessor deserialize = new FunctionProcessor(new JsonDeserializeString<CompoundObject>(CompoundObject.class));
		
		/* Just so that we can see something, we plug a {@link Print} processor at
		 * the end; it will print to the standard output whatever object it receives
		 * from upstream. */
		Print print = new Print();
		
		/* We are now ready to pipe everything together. The interesting bit is
		 * what is <em>not</em> there: notice that we do not connect
		 * <code>up_gateway</code> and <code>dn_gateway</code>. Indeed, these two
		 * processors do not communicate using a BeepBeep pipe like the others;
		 * rather, <code>up_gateway</code> sends its events to <code>dn_gateway</code>
		 * through HTTP requests (i.e., outside of BeepBeep). This is what would
		 * make it possible to put the two halves of this processor chain
		 * (serialize and up_gateway on one side, dn_gateway, deserialize and
		 * print on the other) on two different machines. */
		Connector.connect(serialize, up_gateway);
		Connector.connect(dn_gateway, deserialize);
		Connector.connect(deserialize, print);

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
		Pushable p = serialize.getPushableInput();
		
		/* Let's push some dummy object. After the call to push, the standard
		 * output should display the contents of that object. This is pretty
		 * boring, but think about all the magic that happened in the background:
		 * <ul>
		 * <li>The object was converted into a JSON string</li>
		 * <li>The string was sent over the network through an HTTP request to
		 *   another server...</li>
		 * <li>converted back into an object identical to the original...</li>
		 * <li>and pushed downstream to be handled by the rest of the
		 *   processors as usual.</li>
		 * </ul>
		 * And so far, we've written about 10 lines of code. */
		p.push(new CompoundObject(0, "foo", null));
		
		/* Sleep a little so you have time to look at the console... */
		Thread.sleep(1000);
		
		/* Let's push again. You know the drill. */
		p.push(new CompoundObject(0, "foo", new CompoundObject(6, "z", null)));
		
		/* Once everything is done, we have to stop the servers to free the
		 * TCP ports on your machine. This is done by calling
		 * {@link Processor#stop() stop()} on both processors. */
		up_gateway.stop();
		dn_gateway.stop();
		
		/* That's all folks! */
	}
}
