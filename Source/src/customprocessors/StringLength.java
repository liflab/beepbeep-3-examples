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
import java.util.Set;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SynchronousProcessor;

public class StringLength extends SynchronousProcessor
{
  public StringLength() 
  {
    super(1, 1);
  }

  @Override
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

  @Override
  public void getInputTypesFor(Set<Class<?>> classes, int position) 
  {
    if (position == 0)
    {
      classes.add(String.class);
    }
  }

  @Override
  public Class<?> getOutputType(int position)
  {
    if (position == 0)
    {
      return Number.class;
    }
    return null;
  }
}