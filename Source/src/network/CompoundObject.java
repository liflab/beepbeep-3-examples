/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

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
package network;

/**
 * A dummy object used to show serialization. This class is used in the
 * examples of this package as the type of events to be transmitted over
 * the network.
 */
public class CompoundObject
{
	int a;
	String b;
	CompoundObject c;
	
	protected CompoundObject()
	{
		super();
	}
	
	public CompoundObject(int a, String b, CompoundObject c)
	{
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof CompoundObject))
		{
			return false;
		}
		CompoundObject co = (CompoundObject) o;
		if (this.a != co.a || this.b.compareTo(co.b) != 0)
		{
			return false;
		}
		if ((this.c == null && co.c == null) || (this.c != null && co.c != null && this.c.equals(co.c)))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return "a = " + a + ", b = " + b + ", c = (" + c + ")";
	}
}