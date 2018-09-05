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

public class EuclideanDistance extends SynchronousProcessor
{
  public static final EuclideanDistance instance = new EuclideanDistance();
  
  EuclideanDistance() 
  {
    super(2, 1);
  }

  public boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    Point p1 = (Point) inputs[0];
    Point p2 = (Point) inputs[1];
    double distance = Math.sqrt(Math.pow(p2.x - p1.x, 2)
        + Math.pow(p2.y - p1.y, 2));
    outputs.add(new Object[] {distance});
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    return this;
  }
}
