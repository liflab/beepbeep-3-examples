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
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Insert;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import util.UtilityMethods;

public class LandmarkQuery
{
  public static void main(String[] args)
  {
    ///
    TickerFeed feed = new TickerFeed(10, 20);
    Fork fork1 = new Fork(2);
    Connector.connect(feed, fork1);
    Filter filter = new Filter();
    Connector.connect(fork1, 0, filter, 0);
    ApplyFunction gt_100 = new ApplyFunction(
        new FunctionTree(Numbers.isGreaterThan,
            new FunctionTree(
                Numbers.numberCast, new NthElement(0)),
            new Constant(10)));
    Connector.connect(fork1, 1, gt_100, 0);
    Connector.connect(gt_100, 0, filter, 1);
    Fork fork2 = new Fork(3);
    Connector.connect(filter, fork2);
    Lists.Pack pack = new Lists.Pack();
    Connector.connect(fork2, 0, pack, 0);
    Trim trim = new Trim(1);
    Connector.connect(fork2, 1, trim, 0);
    ApplyFunction eq = new ApplyFunction(new FunctionTree(Booleans.not, 
        new FunctionTree(Equals.instance,
            new FunctionTree(new NthElement(0), StreamVariable.X),
            new FunctionTree(new NthElement(0), StreamVariable.Y))));
    Connector.connect(trim, 0, eq, 0);
    Connector.connect(fork2, 2, eq, 1);
    Insert insert = new Insert(1, false);
    Connector.connect(eq, insert);
    Connector.connect(insert, 0, pack, 1);
    ///

    //! On each list, check if MSFT is greater than 50
    GroupProcessor gp = new GroupProcessor(1, 1);
    ApplyFunction ms_50 = new ApplyFunction(
        new FunctionTree(Booleans.implies,
            new FunctionTree(
                Equals.instance, 
                new Constant("MSFT"), 
                new FunctionTree(
                    new NthElement(1), StreamVariable.X)),
            new FunctionTree(Numbers.isGreaterThan, 
                new FunctionTree(
                    new NthElement(2), StreamVariable.X),
                new Constant(250))));
    gp.addProcessor(ms_50);
    gp.associateInput(0, ms_50, 0);
    Cumulate c_50 = new Cumulate(
        new CumulativeFunction<Boolean>(Booleans.and));
    Connector.connect(ms_50, c_50);
    gp.addProcessor(c_50);
    gp.associateOutput(0, c_50, 0);
    Bags.RunOn ro = new Bags.RunOn(gp);
    Fork fork3 = new Fork(2);
    Connector.connect(pack, fork3);
    Filter f_ms_50 = new Filter();
    Connector.connect(fork3, 0, f_ms_50, 0);
    Connector.connect(fork3, 1, ro, 0);
    Connector.connect(ro, 0, f_ms_50, 1);
    
    // Unpack each day into separate events
    Lists.Unpack up = new Lists.Unpack();
    Connector.connect(f_ms_50, up);
    //!

    Pullable p = up.getPullableOutput();
    while (p.hasNext())
    {
      System.out.println(UtilityMethods.print(p.pull()));
    }
  }
}
