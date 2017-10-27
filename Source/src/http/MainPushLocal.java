package http;

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

public class MainPushLocal 
{
	public static void main(String[] args) throws ConnectorException, ProcessorException, InterruptedException
	{
		// Instantiate and pipe the processors 
		FunctionProcessor serialize = new FunctionProcessor(new JsonSerializeString());
		HttpUpstreamGateway up_gateway = new HttpUpstreamGateway("http://localhost:12144/push");
		HttpDownstreamGateway dn_gateway = new HttpDownstreamGateway(12144, "/push", Method.POST);
		FunctionProcessor deserialize = new FunctionProcessor(new JsonDeserializeString<CompoundObject>(CompoundObject.class));
		Print print = new Print();
		print.setSeparator("\n");
		Connector.connect(serialize, up_gateway);
		Connector.connect(up_gateway, dn_gateway);
		Connector.connect(dn_gateway, deserialize);
		Connector.connect(deserialize, print);
		
		// Start both gateways so they can listen to requests
		up_gateway.start();
		dn_gateway.start();
		
		// Push events on one end
		Pushable p = serialize.getPushableInput();
		p.push(new CompoundObject(0, "foo", null));
		Thread.sleep(1000);
		p.push(new CompoundObject(0, "foo", new CompoundObject(6, "z", null)));
		// Stop both gateways
		up_gateway.stop();
		dn_gateway.stop();
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
