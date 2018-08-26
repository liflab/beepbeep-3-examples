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
package xml;

import ca.uqac.lif.cep.xml.XPathFunction;
import ca.uqac.lif.cep.fol.ForAll;
import ca.uqac.lif.cep.functions.ContextVariable;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Strings;
import ca.uqac.lif.cep.xml.ParseXml;
import ca.uqac.lif.xml.XmlElement;

/**
 * Evaluate JPath expressions on JSON elements.
 * @author Sylvain Hallé
 */
public class ContextExample
{
  public static void main(String[] args)
  {
    ///
    

    // The domain function fetches the set all the values of elements <b>;
    // the XPath function produces TextElements, which are converted to
    // strings
    FunctionTree dom_f = new FunctionTree(
        new Bags.ApplyToAll(Numbers.numberCast),
        new FunctionTree(
            new XPathFunction("doc/a/b/text()"), StreamVariable.X));
    ForAll fa = new ForAll("x", dom_f,
        new FunctionTree(Numbers.isLessThan,
            new ContextVariable("x"),
            new FunctionTree(Numbers.numberCast, 
                new FunctionTree(Bags.anyElement, 
                    new XPathFunction("doc/a[b=$x]/c/text()")))
            ));
    ///
    
    //!
    // Let us evaluate this on a first document
    XmlElement x = ParseXml.instance.getValue("<doc>\n"
        + "<a><b>1</b><c>10</c></a>\n"
        + "<a><b>2</b><c>15</c></a>\n"
        + "<d>123</d>\n"
        + "</doc>");
    Object[] out = new Object[1];
    fa.evaluate(new Object[] {x}, out);
    System.out.println(out[0]);

    // Another one
    x = ParseXml.instance.getValue("<doc>\n"
        + "<a><b>1</b><c>10</c></a>\n"
        + "<a><b>20</b><c>15</c></a>\n"
        + "<d>123</d>\n"
        + "</doc>");
    fa.evaluate(new Object[] {x}, out);
    System.out.println(out[0]);
    //!
  }
}
