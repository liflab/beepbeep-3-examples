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

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Computes the slope between two successive points in a data stream
 * (which is simply the difference between point x<sub>i+1</sub> and point
 * x<sub>i</sub>).
 */
public class Slope extends GroupProcessor 
{
	public Slope() 
	{
		super(1, 1);
		Fork f = new Fork(2);
		associateInput(0, f, 0);
		Trim t = new Trim(1);
		Connector.connect(f, 0, t, 0);
		ApplyFunction minus = new ApplyFunction(Numbers.subtraction);
		Connector.connect(t, 0, minus, 0);
		Connector.connect(f, 1, minus, 1);
		associateOutput(0, minus, 0);
		addProcessors(f, t, minus);
	}
}
