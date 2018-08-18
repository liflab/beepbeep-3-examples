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
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.ContextVariable;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Duplicating processors and observing what happens to their
 * Context object.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class DuplicateContext 
{
  public static void main(String[] args)
  {
    /// We first create an ApplyFunction that simply adds
    // the value of some context element to the current event
    ApplyFunction f1 = new ApplyFunction(new FunctionTree(
        Numbers.addition, StreamVariable.X, new ContextVariable("foo")));
    Connector.connect(f1, new Print()
        .setPrefix("f1: ").setSeparator("\n"));

    // We then feed a few events to each of them
    Pushable p_f1 = f1.getPushableInput();
    f1.setContext("foo", 10);
    p_f1.push(3).push(1);
    
    // Let us now duplicate sum1 and push events to the copy
    ApplyFunction f2 = (ApplyFunction) f1.duplicate();
    Connector.connect(f2, new Print()
        .setPrefix("f2: ").setSeparator("\n"));
    Pushable p_f2 = f2.getPushableInput();
    p_f2.push(2).push(7).push(1);
    ///
  }
}
