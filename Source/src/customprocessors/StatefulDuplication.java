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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.QueueSource;

public class StatefulDuplication
{

  public static void main(String[] args)
  {
    System.out.println("--- Version one ---");
    {
      ///
      QueueSource src1 = new QueueSource();
      src1.setEvents(2, 1);
      Stuttering s1 = new Stuttering();
      Connector.connect(src1, s1);
      Pullable p1 = s1.getPullableOutput();
      System.out.println("Call to pull on p1: " + p1.pull());
      ///
      //*
      QueueSource src2 = new QueueSource();
      src2.setEvents(3, 1);
      Stuttering s2 = s1.duplicate(true);
      Connector.connect(src2, s2);
      Pullable p2 = s2.getPullableOutput();
      System.out.println("Call to pull on p2: " + p2.pull());
      //*
    }
    System.out.println("\n --- Version two ---");
    {
      //!
      QueueSource src1 = new QueueSource();
      src1.setEvents(2, 1);
      StutteringCopy s1 = new StutteringCopy();
      Connector.connect(src1, s1);
      Pullable p1 = s1.getPullableOutput();
      System.out.println("Call to pull on p1: " + p1.pull());
      QueueSource src2 = new QueueSource();
      src2.setEvents(3, 1);
      StutteringCopy s2 = s1.duplicate(true);
      Connector.connect(src2, s2);
      Pullable p2 = s2.getPullableOutput();
      System.out.println("Call to pull on p2: " + p2.pull());
      //!
    }
  }
}
