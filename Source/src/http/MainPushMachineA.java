package http;

import java.util.Scanner;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.http.HttpUpstreamGateway;
import ca.uqac.lif.cep.serialization.JsonSerializeString;
import ca.uqac.lif.cep.tmf.QueueSource;

public class MainPushMachineA 
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
		
		// Push events out
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
