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
package ltl;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.ltl.Globally;

/**
 * Basic usage of LTL's {@link Globally} processor. It is illustrated
 * as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/ltl/GloballySimple.png" alt="Processor graph">
 * <p>
 * @author Sylvain Hallé
 */
public class GloballySimple
{
  public static void main(String[] args)
  {
    ///
    Globally g = new Globally();
    Print print = new Print();
    print.setPrefix("Output: ").setSeparator("\n");
    Connector.connect(g, print);
    Pushable p = g.getPushableInput();
    System.out.println("Pushing true");
    p.push(true);
    System.out.println("Pushing true");
    p.push(true);
    ///
    //*
    System.out.println("Pushing false");
    p.push(false);
    System.out.println("Pushing true");
    p.push(true);
    //*
  }

}
