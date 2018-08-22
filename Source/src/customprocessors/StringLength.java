package customprocessors;

import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;

public class StringLength extends SynchronousProcessor
{
	public StringLength() 
	{
		super(1, 1);
	}

	public boolean compute(Object[] inputs, Queue<Object[]> outputs) 
	{
		int length = ((String) inputs[0]).length();
		return outputs.add(new Object[]{length});
	}

	@Override
	public Processor duplicate(boolean with_state) 
	{
		return new StringLength();
	}

}