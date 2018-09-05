/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
	public Stuttering duplicate(boolean with_state) 
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
