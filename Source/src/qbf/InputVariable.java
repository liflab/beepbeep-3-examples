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
package qbf;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.tmf.Prefix;
import ca.uqac.lif.cep.tmf.Stutter;

import static java.lang.Math.pow;

public class InputVariable extends GroupProcessor
{
	public InputVariable(int n, int i)
	{
		super(1, 1);
		Prefix p = new Prefix((int) pow(2, i));
		Stutter s = new Stutter((int) pow(2, n - i));
		Connector.connect(p, s);
		addProcessors(p, s);
		associateInput(0, p, 0);
		associateOutput(0, s, 0);
	}
}
