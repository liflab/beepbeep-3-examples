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

import ca.uqac.lif.cep.xml.ParseXml;
import ca.uqac.lif.xml.XmlElement;
import java.util.List;

/**
 * Parse XML strings.
 * @author Sylvain Hallé
 */
public class Parsing
{
  public static void main(String[] args)
  {
    ///
    ParseXml parse = ParseXml.instance;
    Object[] out = new Object[1];
    parse.evaluate(new Object[]{
      "<doc><a>123</a><b>foo</b></doc>"}, out);
    XmlElement x = (XmlElement) out[0];
    System.out.println(x);
    ///
    parse.evaluate(new Object[]{
    "<a"}, out);
    System.out.println(out[0].getClass());

    //*
    List<XmlElement> ch = (List<XmlElement>) x.getChildren();
    XmlElement e = ch.get(1);
    System.out.println(e.getName() + ", " + e.getTextElement());
    //*
  }
}
