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
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Duplicating processors without preserving their state.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class DuplicateNoState 
{
  public static void main(String[] args)
  {
    /// We first create a cumulative processor and a simple GroupProcessor
    Cumulate sum1 = new Cumulate(
        new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(sum1, new Print()
        .setPrefix("sum1: ").setSeparator("\n"));

    // We then feed a few events to each of them
    Pushable p_sum1 = sum1.getPushableInput();
    p_sum1.push(3).push(1).push(4);
    ///
    
    //* Let us now duplicate sum1 and push events to the copy
    Cumulate sum2 = (Cumulate) sum1.duplicate();
    Connector.connect(sum2, new Print()
        .setPrefix("sum2: ").setSeparator("\n"));
    Pushable p_sum2 = sum2.getPushableInput();
    p_sum2.push(2).push(7).push(1);
    //*
  }
}
