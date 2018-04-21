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
package voyager;

import ca.uqac.lif.cep.functions.BinaryFunction;

/**
 * Receives two arguments: one of a year number, and another with a number of
 * days in the year. Returns as its output 
 * the number of days elapsed since 1/1/1977.
 */
public class FormatDate extends BinaryFunction<Number,Number,Number>
{
	/**
	 * The single publicly accessible instance of this function
	 */
	public static final FormatDate instance = new FormatDate();
	
	protected FormatDate()
	{
		super(Number.class, Number.class, Number.class);
	}

	@Override
	public Number getValue(Number x, Number y) 
	{
		return (x.intValue() - 1977) * 365 + y.intValue();
	}
}
