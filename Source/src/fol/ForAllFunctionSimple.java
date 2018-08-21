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
import ca.uqac.lif.cep.util.Numbers;
import java.util.ArrayList;
import java.util.List;

public class ForAllFunctionSimple
{

  public static void main(String[] args)
  {
    ///
    Function f = new FunctionTree(Numbers.isEven, new ContextVariable("x"));
    ForAll fa = new ForAll("x", f);
    List<Number> nums = new ArrayList<Number>();
    nums.add(2);
    nums.add(6);
    Object[] outputs = new Object[1];
    fa.evaluate(new Object[]{nums}, outputs);
    System.out.println(outputs[0]);
    nums.add(3);
    fa.evaluate(new Object[]{nums}, outputs);
    System.out.println(outputs[0]);
    ///
  }

}
