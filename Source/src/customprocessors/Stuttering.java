package customprocessors;

import java.util.Queue;

import ca.uqac.lif.cep.*;
import ca.uqac.lif.cep.tmf.QueueSource;

public class Stuttering extends SynchronousProcessor 
{
	public Stuttering() 
	{
		super(1, 1);
	}

	@Override
	public boolean compute(Object[] inputs, Queue<Object[]> outputs) 
	{
	  System.out.println("Call to compute");
		Number n = (Number) inputs[0];
		for (int i = 0; i < n.intValue(); i++) 
		{
			outputs.add(new Object[] {inputs[0]});
		}	
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state) 
	{
		return new Stuttering();
	}
	
	public static void main(String[] args)
	{
	  ///
	  QueueSource src = new QueueSource();
	  src.setEvents(1, 2, 1);
	  Stuttering s = new Stuttering();
	  Connector.connect(src, s);
	  Pullable p = s.getPullableOutput();
	  for (int i = 0; i < 4; i++)
	  {
	    System.out.println("Call to pull: " + p.pull());
	  }
	  ///
	}
}
