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
package auction;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Maps;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Strings;

public class MonotonicBid
{

  public static void main(String[] args)
  {
    ReadLines lines = new ReadLines(MonotonicBid.class.getResourceAsStream("auction1.csv"));
    ApplyFunction split = new ApplyFunction(new Strings.SplitString(","));
    Connector.connect(lines, split);
    ///
    Fork f = new Fork(2);
    Connector.connect(split, f);
    Filter filter =  new Filter();
    Connector.connect(f, 0, filter, 0);
    ApplyFunction is_bid = new ApplyFunction(
        new FunctionTree(Equals.instance,
            new Constant("Bid"),
            new NthElement(0)));
    Connector.connect(f, 1, is_bid, 0);
    Connector.connect(is_bid, 0, filter, 1);
    
    GroupProcessor bid_amount = new GroupProcessor(1, 1);
    {
      ApplyFunction get_amt = new ApplyFunction(new NthElement(2));
      Fork b_f = new Fork(2);
      Connector.connect(get_amt, b_f);
      ApplyFunction gt = new ApplyFunction(Numbers.isLessThan);
      Connector.connect(b_f, 0, gt, 0);
      Trim trim = new Trim(1);
      Connector.connect(b_f, 1, trim, 0);
      Connector.connect(trim, 0, gt, 1);
      bid_amount.associateInput(0, get_amt, 0);
      bid_amount.associateOutput(0, gt, 0);
      bid_amount.addProcessors(get_amt, b_f, gt, trim);
    }
    Slice slice = new Slice(new NthElement(1), bid_amount);
    Connector.connect(filter, slice);
    ApplyFunction values = new ApplyFunction(Maps.values);
    Connector.connect(slice, values);
    Bags.RunOn and = new Bags.RunOn(new Cumulate(
        new CumulativeFunction<Boolean>(Booleans.and)));
    Connector.connect(values, and);
    ///
    Pullable p = values.getPullableOutput();
    while (p.hasNext())
    {
      System.out.println(p.next());
    }

  }
}
