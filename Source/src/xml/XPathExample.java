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
import ca.uqac.lif.cep.xml.ParseXml;
import ca.uqac.lif.xml.XmlElement;

/**
 * Evaluate JPath expressions on JSON elements.
 * @author Sylvain Hallé
 */
public class XPathExample
{
  public static void main(String[] args)
  {
    ///
    Object[] out = new Object[1];
    ParseXml.instance.evaluate(new Object[]{
        "<doc>\n"
            + "<a><b>1</b><c>10</c></a>\n"
            + "<a><b>2</b><c>15</c></a>\n"
            + "<d>123</d>\n"
            + "</doc>"}, out);
    XmlElement x = (XmlElement) out[0];
    System.out.println(
        new XPathFunction("doc/d/text()").getValue(x));
    System.out.println(
        new XPathFunction("doc/a/b").getValue(x));
    System.out.println(
        new XPathFunction("doc/a[b=2]/c").getValue(x));
    ///
  }
}
