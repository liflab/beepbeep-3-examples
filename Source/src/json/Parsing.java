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

import ca.uqac.lif.cep.json.ParseJson;
import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonList;
import ca.uqac.lif.json.JsonMap;
import ca.uqac.lif.json.JsonNumber;

/**
 * Parse JSON strings.
 * @author Sylvain Hallé
 */
public class Parsing
{
  public static void main(String[] args)
  {
    ///
    ParseJson parse = ParseJson.instance;
    Object[] out = new Object[1];
    parse.evaluate(new Object[]{
        "{\"a\" : 123, \"b\" : false, \"c\" : [4,5,6]}"}, out);
    JsonElement j = (JsonElement) out[0];
    System.out.println(j);
    parse.evaluate(new Object[]{
    "{\"a\" : "}, out);
    System.out.println(out[0].getClass());
    ///
    
    //*
    JsonMap map = (JsonMap) j;
    JsonNumber n = (JsonNumber) map.get("a");
    System.out.println(n.numberValue());
    JsonList l = (JsonList) map.get("c");
    System.out.println(l.get(1));
    //*
  }
}
