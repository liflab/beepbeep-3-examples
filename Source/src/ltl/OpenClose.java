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

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.ApplyFunctionLazy;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.ltl.Eventually;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;

/**
 * Using an LTL expression to filter events in an input stream.
 * It is illustrated as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/ltl/OpenClose.png" alt="Processor graph">
 * <p>
 * @author Sylvain Hallé
 *
 */
public class OpenClose
{

  public static void main(String[] args)
  {
    ///
    Fork f1 = new Fork(2);
    ApplyFunctionLazy imp = new ApplyFunctionLazy(
        new FunctionTree(Booleans.implies,
            new FunctionTree(Equals.instance, 
                StreamVariable.X, new Constant("open")),
            StreamVariable.Y));
    ApplyFunction close = new ApplyFunction(
        new FunctionTree(Equals.instance,
            StreamVariable.X, new Constant("close")));
    Eventually e = new Eventually();
    Fork f2 = new Fork(2);
    Connector.connect(f1, BOTTOM, f2, INPUT);
    Connector.connect(f2, TOP, imp, TOP);
    Connector.connect(f2, BOTTOM, close, INPUT);
    Connector.connect(close, e);
    Connector.connect(e, OUTPUT, imp, BOTTOM);
    Filter filter = new Filter();
    Connector.connect(f1, TOP, filter, TOP);
    Connector.connect(imp, OUTPUT, filter, BOTTOM);
    Print print = new Print();
    print.setPrefix("Output: ").setSeparator("\n");
    Connector.connect(filter, print);
    Pushable p = f1.getPushableInput();
    System.out.println("Pushing nop");
    p.push("nop");
    System.out.println("Pushing open");
    p.push("open");
    System.out.println("Pushing read");
    p.push("read");
    System.out.println("Pushing close");
    p.push("close");
    ///
  }

}
