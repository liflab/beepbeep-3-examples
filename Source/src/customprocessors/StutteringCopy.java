package customprocessors;

import ca.uqac.lif.cep.*;
import ca.uqac.lif.cep.tmf.QueueSource;

public class StutteringCopy extends Stuttering 
{
	public StutteringCopy() 
	{
		super();
	}

	@Override
	public StutteringCopy duplicate(boolean with_state) 
	{
		StutteringCopy s = new StutteringCopy();
		if (with_state)
		{
		  super.duplicateInto(s);
		}
		return s;
	}
	
	public static void main(String[] args)
	{
	  ///
	  QueueSource src = new QueueSource();
	  src.setEvents(2, 1);
	  Stuttering s1 = new Stuttering();
	  Connector.connect(src, s1);
	  Pullable p1 = s1.getPullableOutput();
	  for (int i = 0; i < 4; i++)
	  {
	    System.out.println("Call to pull: " + p.pull());
	  }
	  ///
	}
}
