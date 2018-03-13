/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hall�

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
package basic;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.Tank;
import ca.uqac.lif.cep.util.Numbers;

public class WithTank 
{
	public static void main(String[] args) 
	{
		///
		ApplyFunction to_number = new ApplyFunction(Numbers.numberCast);
		Tank tank = new Tank();
		Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(to_number, tank, sum);
		///
		//!
		Pushable ps = to_number.getPushableInput();
		Pullable pl = sum.getPullableOutput();
		ps.push("1");
		ps.push("2");
		System.out.println(pl.pull());
		System.out.println(pl.pull());
		System.out.println(pl.pull());
		//!
	}

}
