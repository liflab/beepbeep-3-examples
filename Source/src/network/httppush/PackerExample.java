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

import java.util.LinkedList;

import util.UtilityMethods;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.http.HttpDownstreamGateway;
import ca.uqac.lif.cep.http.HttpUpstreamGateway;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.serialization.JsonDeserializeString;
import ca.uqac.lif.cep.serialization.JsonSerializeString;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.TimeDecimate;
import ca.uqac.lif.cep.util.Lists.TimePack;
import ca.uqac.lif.cep.util.Lists.Unpack;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.jerrydog.RequestCallback.Method;

/**
 * Use a packer to send events in batch and reduce the number of HTTP
 * requests.
 * <p>
 * <img
 *   src="{@docRoot}/doc-files/network/httppush/PackerExample.png"
 *   alt="Processor graph">
 * <p>
 * In this example, we send a relatively fast flow of events (about 1,000
 * per second) from one host to another using HTTP gateways
 * (see {@link PushLocalSerialize} for an explanation of gateways). If we used
 * the {@link HttpUpstreamGateway} directly, this would result in one
 * HTTP request-response cycle for each event to be pushed over the network.
 * However, each request has an associated overhead, which places an upper
 * bound on the number of requests per second that can be sent.
 * <p>
 * The {@link TimePack} processor can be used to reduce the number of
 * such requests. The packer accumulates events for a predetermined amount
 * of time (say, one second), and outputs all accumulated events as a single
 * <code>List</code> object when the time interval is expired. When coupled
 * with a JSON serializer (to transform that list into a JSON string) and
 * an {@link HttpUpstreamGateway}, this results in fewer HTTP requests, a
 * smaller overhead, and hence an increased throughput.  
 * <p>
 * Setting the packer to a relatively long interval (e.g. 1 second) will
 * be such that a single HTTP request will contain a batch of about 1,000
 * events at a time. The output of the program should look like this:
 * <pre>
 * Pushing 1000 events per second. Hit Ctrl+C to end.
 * 1,717,1574,2432,3280,...
 * </pre>
 * In contrast, setting the packer to a short time interval
 * (e.g. 2 milliseconds) will result in the packer making much smaller
 * bundles, which in turn will make the HTTP gateway send more HTTP requests.
 * The output of the program should look like this:
 * <pre>
 * Pushing 1000 events per second. Hit Ctrl+C to end.
 * 1,393,830,1375,1966,...
 * </pre>
 * Since every number corresponds to the cumulative number of events received
 * at every passing second, one can see that sending too many HTTP requests
 * results in a slower throughput than packing events and sending them in bulk
 * periodically. 
 * 
 * @author Sylvain Hallé
 */
public class PackerExample 
{
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws ProcessorException, InterruptedException
	{
		/* Let us assume that "Machine A" produces a stream of increasing
		 * integers. */
		QueueSource source = new QueueSource();
		source.addEvent(1);
		Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(source, sum);
		
		/* Connect the output to a packer, and give it a time interval
		 * 1 second. */
		TimePack packer = new TimePack();
		packer.setInterval(1000);
		Connector.connect(sum, packer);

		/* Connect the output of the packer to a serializer, and the serializer
		 * to an {@link HttpUpstreamGateway}. */
		ApplyFunction serialize = new ApplyFunction(new JsonSerializeString());
		Connector.connect(packer, serialize);
		HttpUpstreamGateway up_gateway = new HttpUpstreamGateway("http://localhost:12149/push");
		Connector.connect(serialize, up_gateway);
		
		/* We now move on to Machine B. We create in a row an
		 * {@link HttpDownstreamGateway}, a deserializser that will convert the
		 * string back into a list of events, and an unpacker that will push
		 * each element of the list as individual events. */
		HttpDownstreamGateway dn_gateway = new HttpDownstreamGateway(12149, "/push", Method.POST);
		ApplyFunction deserialize = new ApplyFunction(new JsonDeserializeString<LinkedList>(LinkedList.class));
		Connector.connect(dn_gateway, deserialize);
		Unpack unpacker = new Unpack();
		Connector.connect(deserialize, unpacker);
		
		/* We only keep one event about every second, using the TimeDecimate
		 * processor, and print it to the screen. */
		TimeDecimate every_second = new TimeDecimate(1000000000); 
		Connector.connect(unpacker, every_second);
		Print print = new Print();
		//Connector.connect(unpacker, print);
		Connector.connect(every_second, print);
		
		/* We start the gateways and the packer. */
		packer.start();
		up_gateway.start();
		dn_gateway.start();

		/* We are now ready to push events and see what happens. This loop sends
		 * about 100 events per second. */
		System.out.println("Pushing 1000 events per second. Hit Ctrl+C to end.");
		for (long n = 0; n < 10000; n++)
		{
			source.push();
			UtilityMethods.pause(1);
		}
				
		/* Stop the servers to free the TCP ports on your machine. */
		up_gateway.stop();
		dn_gateway.stop();
		
		/* That's all folks! */
	}
}
