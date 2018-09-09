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
package hl7;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Numbers;

public class StdevExample
{

  public static void main(String[] args)
  {
    Fork f = new Fork(4);
    StatMoment avg = new StatMoment(1);
    Connector.connect(f, 1, avg, 0);
    ApplyFunction sub = new ApplyFunction(Numbers.subtraction);
    Connector.connect(f, 0, sub, 0);
    Connector.connect(avg, 0, sub, 1);
    ApplyFunction div = new ApplyFunction(Numbers.division);
    Stdev std = new Stdev();
    Connector.connect(f, 2, std, 0);
    Connector.connect(sub, 0, div, 0);
    Connector.connect(std, 0, div, 1);
    ApplyFunction gt = new ApplyFunction(Numbers.isGreaterThan);
    TurnInto one = new TurnInto(1);
    Connector.connect(f, 3, one, 0);
    Connector.connect(div, 0, gt, 0);
    Connector.connect(one, 0, gt, 1);
    Fork f2 = new Fork(2);
    ApplyFunction and = new ApplyFunction(Booleans.and);
    Connector.connect(f2, 0, and, 0);
    Trim tr = new Trim(1);
    Connector.connect(f2, 1, tr, 0);
    Connector.connect(tr, 0, and, 1);
  }

}
