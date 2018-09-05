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

import ca.uqac.lif.cep.*;
import java.util.Queue;

public class SplitPoint extends SynchronousProcessor 
{
  public SplitPoint() 
  {
    super(1, 2);
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    Point p = (Point) inputs[0];
    outputs.add(new Object[] {p.x, p.y});
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    return new SplitPoint();
  }
}
