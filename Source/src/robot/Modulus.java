/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2022 Sylvain Hallé

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
package robot;

import java.util.Vector;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Function that calculates the modulus of a two dimensional vector.
 * @author Sylvain Hallé
 */
@SuppressWarnings("rawtypes")
public class Modulus extends UnaryFunction<Vector,Float>
{
  /**
   * A publicly visible instance of the function.
   */
  /*@ non_null @*/ public static final Modulus instance = new Modulus();
  
  /**
   * Creates a new instance of the function.
   */
  protected Modulus()
  {
    super(Vector.class, Float.class);
  }

  @Override
  public Float getValue(Vector v)
  {
    float x = (Float) v.get(0);
    float y = (Float) v.get(1);
    return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
  }
  
  @Override
  public String toString()
  {
    return "||";
  }
}