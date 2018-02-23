package voyager;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Numbers;

public class Slope extends GroupProcessor 
{
	public Slope() 
	{
		super(1, 1);
		Fork f = new Fork(2);
		associateInput(0, f, 0);
		Trim t = new Trim(1);
		Connector.connect(f, 0, t, 0);
		ApplyFunction minus = new ApplyFunction(Numbers.subtraction);
		Connector.connect(t, 0, minus, 0);
		Connector.connect(f, 1, minus, 1);
		associateOutput(0, minus, 0);
		addProcessors(f, t, minus);
	}
	
	

}
