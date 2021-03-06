/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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
package ltl;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.ltl.Next;

/**
 * Basic usage of LTL's {@link Next} processor. It is illustrated
 * as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/ltl/NextSimple.png" alt="Processor graph">
 * <p>
 * @author Sylvain Hallé
 */
public class NextSimple
{

  public static void main(String[] args)
  {
    ///
    Next n = new Next();
    Print print = new Print();
    print.setPrefix("Output: ").setSeparator("\n");
    Connector.connect(n, print);
    Pushable p = n.getPushableInput();
    System.out.println("Pushing true");
    p.push(true);
    System.out.println("Pushing true");
    p.push(true);
    System.out.println("Pushing false");
    p.push(false);
    System.out.println("Pushing true");
    p.push(true);
    ///
  }

}
