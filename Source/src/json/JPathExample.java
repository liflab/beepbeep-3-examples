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
package json;

import ca.uqac.lif.cep.json.JPathFunction;
import ca.uqac.lif.cep.json.ParseJson;
import ca.uqac.lif.json.JsonElement;

/**
 * Evaluate JPath expressions on JSON elements.
 * @author Sylvain Hallé
 */
public class JPathExample
{
  public static void main(String[] args)
  {
    ///
    Object[] out = new Object[1];
    ParseJson.instance.evaluate(new Object[]{
        "{\"a\" : 123, \"b\" : false, \"c\" : [4,5,6]}"}, out);
    JsonElement j = (JsonElement) out[0];
    JPathFunction f1 = new JPathFunction("a");
    f1.evaluate(new Object[]{j}, out);
    System.out.println(out[0]);
    ///
    
    //*
    JPathFunction f2 = new JPathFunction("c[1]");
    f2.evaluate(new Object[]{j}, out);
    System.out.println(out[0]);
    //*
  }
}
