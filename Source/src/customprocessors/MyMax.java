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

public class MyMax extends SynchronousProcessor
{
	Number last = null;

	public MyMax() 
	{
		super(1, 1);
	}

	public boolean compute(Object[] inputs, Queue<Object[]> outputs) {
		Number current = (Number) inputs[0];
		Number output;
		if (last != null) {
			output = Math.max(last.floatValue(), current.floatValue());
			last = current;
			outputs.add(new Object[]{output});
		}
		else {
			last = current;
		}
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state)
	{
	  MyMax mm = new MyMax();
	  if (with_state)
	  {
	    mm.last = this.last;
	  }
		return mm;
	}
}