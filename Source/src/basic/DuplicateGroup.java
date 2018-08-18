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
package basic;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Stutter;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Duplicating processors without preserving their state.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class DuplicateGroup 
{
  public static void main(String[] args)
  {
    /// We first create a GroupProcessor
    QueueSource source1 = new QueueSource().setEvents(3, 1, 4, 1, 5);
    GroupProcessor gp1 = new GroupProcessor(1, 1);
    {
      Stutter st = new Stutter(2);
      Cumulate sum = new Cumulate(
          new CumulativeFunction<Number>(Numbers.addition));
      Connector.connect(st, sum);
      gp1.addProcessors(st, sum);
      gp1.associateInput(0, st, 0);
      gp1.associateOutput(0, sum, 0);
    }
    Connector.connect(source1, gp1);
    
    // We pull one event from gp1
    Pullable p_gp1 = gp1.getPullableOutput();
    System.out.println(p_gp1.pull());
    ///

    //! Let us clone gp1 and connect it to a new source
    QueueSource source2 = new QueueSource().setEvents(2, 7, 1, 8);
    GroupProcessor gp2 = gp1.duplicate(true);
    Connector.connect(source2, gp2);
    Pullable p_gp2 = gp2.getPullableOutput();
    System.out.println(p_gp2.pull());
    //!
  }
}
