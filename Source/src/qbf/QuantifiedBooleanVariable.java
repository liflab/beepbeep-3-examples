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
import ca.uqac.lif.cep.functions.BinaryFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.Booleans;

public abstract class QuantifiedBooleanVariable extends GroupProcessor
{
	public QuantifiedBooleanVariable(BinaryFunction<Boolean,Boolean,Boolean> operator)
	{
		super(1, 1);
		Window w = new Window(new Cumulate(new CumulativeFunction<Boolean>(operator)), 2);
		CountDecimate d = new CountDecimate(2);
		Connector.connect(w, d);
		addProcessors(w, d);
		associateInput(0, w, 0);
		associateOutput(0, d, 0);
	}
	
	public static class ExistentialVariable extends QuantifiedBooleanVariable
	{
		public ExistentialVariable()
		{
			super(Booleans.or);
		}
	}
	
	public static class UniversalVariable extends QuantifiedBooleanVariable
	{
		public UniversalVariable()
		{
			super(Booleans.and);
		}
	}
}
