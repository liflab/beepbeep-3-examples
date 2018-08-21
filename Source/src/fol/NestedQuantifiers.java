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
package fol;

import ca.uqac.lif.cep.fol.ForAll;
import ca.uqac.lif.cep.functions.ContextVariable;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Size;
import java.util.HashSet;
import java.util.Set;

public class NestedQuantifiers
{
  public static void main(String[] args)
  {
    ///
    Function f = new FunctionTree(Equals.instance,
        new FunctionTree(Size.instance, new ContextVariable("x")),
        new FunctionTree(Size.instance, new ContextVariable("y")));
    ForAll fa2 = new ForAll("y", f);
    ForAll fa1 = new ForAll("x", fa2);
    ///
    //*
    Set<String> strings = new HashSet<String>();
    strings.add("foo");
    strings.add("bar");
    Object[] outputs = new Object[1];
    fa1.evaluate(new Object[]{strings}, outputs);
    System.out.println(outputs[0]);
    strings.add("bazz");
    fa1.evaluate(new Object[]{strings}, outputs);
    System.out.println(outputs[0]);
    //*
  }

}
