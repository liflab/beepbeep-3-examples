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

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.TOP;
import static ca.uqac.lif.cep.Connector.OUTPUT;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.ApplyFunctionPartial;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.ltl.Next;
import ca.uqac.lif.cep.ltl.Until;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;

/**
 * Basic usage of LTL's {@link Until} processor. It is illustrated
 * as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/ltl/UntilSimple.png" alt="Processor graph">
 * <p>
 * @author Sylvain Hallé
 */
public class Nested
{
  public static void main(String[] args)
  {
    Fork fork = new Fork(5);
    ApplyFunctionPartial imp = new ApplyFunctionPartial(new FunctionTree(Booleans.implies,
        new FunctionTree(Equals.instance, StreamVariable.X, new Constant("a")),
        StreamVariable.Y
        ));
    Connector.connect(fork, 1, imp, TOP);
    ApplyFunction f_nbc = new ApplyFunction(new FunctionTree(Booleans.not, 
        new FunctionTree(Booleans.and, 
            new FunctionTree(Equals.instance, StreamVariable.X, new Constant("b")), 
            StreamVariable.Y)));
    Connector.connect(fork, 2, f_nbc, TOP);
    ApplyFunction f_c = new ApplyFunction(
        new FunctionTree(Equals.instance, StreamVariable.X, new Constant("c")));
    Connector.connect(fork, 3, f_c, INPUT);
    Next next = new Next();
    Connector.connect(f_c, next);
    Connector.connect(next, OUTPUT, f_nbc, BOTTOM);
    Until until = new Until();
    Connector.connect(f_nbc, OUTPUT, until, TOP);
    ApplyFunction f_d = new ApplyFunction(
        new FunctionTree(Equals.instance, StreamVariable.X, new Constant("d")));
    Connector.connect(fork, 4, f_d, INPUT);
    Connector.connect(f_d, OUTPUT, until, BOTTOM);
    Connector.connect(until, OUTPUT, imp, BOTTOM);
    Print print_out = new Print().setPrefix("Output: ").setSeparator("\n");
    Connector.connect(imp, print_out);
    Print print_in = new Print().setPrefix("Pushing: ").setSeparator("\n");
    Connector.connect(fork, 0, print_in, INPUT);
    Pushable p = fork.getPushableInput();
    p.push("c");
    p.push("d");
    p.push("a");
    p.push("c");
    p.push("b");
    p.push("d");
    p.push("f");
    ///
  }
}
