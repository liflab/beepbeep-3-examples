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
import ca.uqac.lif.cep.functions.ApplyFunctionPartial;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.util.Numbers;

public class PartialEvaluation
{
  public static void main(String[] args)
  {
    ///
    ApplyFunctionPartial af = 
        new ApplyFunctionPartial(Numbers.multiplication);
    Print print = new Print();
    Connector.connect(af, print);
    Pushable p1 = af.getPushableInput(0);
    Pushable p2 = af.getPushableInput(1);
    ///
    
    //!
    p1.push(3);
    p2.push(1);
    //!
    
    //*
    p1.push(0);
    //*
    
    //&
    p1.push(6);
    p2.push(9);
    p2.push(5);
    //&
  }
}
