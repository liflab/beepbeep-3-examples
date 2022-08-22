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
package stockticker;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.UtilityMethods;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Prefix;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.NthElement;

public class SnapshotQuery
{
  public static void main(String[] args)
  {
    ///
    TickerFeed feed = new TickerFeed();
    Fork fork = new Fork(2);
    Connector.connect(feed, fork);
    Filter filter = new Filter();
    Connector.connect(fork, 0, filter, 0);
    ApplyFunction is_msft = new ApplyFunction(
        new FunctionTree(Equals.instance,
            new Constant("MSFT"),
            new NthElement(1)));
    Connector.connect(fork, 1, is_msft, 0);
    Connector.connect(is_msft, 0, filter, 1);
    Prefix pref = new Prefix(5);
    Connector.connect(filter, pref);
    ///
    Pullable p = pref.getPullableOutput();
    while (p.hasNext())
    {
      System.out.println(UtilityMethods.print(p.pull()));
    }
  }
}
