package http;

import java.util.Scanner;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.cli.Print;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.http.HttpDownstreamGateway;
import ca.uqac.lif.cep.serialization.JsonDeserializeString;
import ca.uqac.lif.jerrydog.RequestCallback.Method;

public class MainPushMachineB 
{
	public static void main(String[] args) throws ConnectorException, ProcessorException, InterruptedException
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
		print.setSeparator("\n");
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
