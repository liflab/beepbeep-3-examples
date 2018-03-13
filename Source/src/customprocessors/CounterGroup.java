/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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
package customprocessors;

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.connect;

import java.util.Vector;

import util.UtilityMethods;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Numbers;

/**
 * This processor simply generates the trace of numbers 0, 1, 2, ...
 * This is one of two possible ways of writing such a counter: by
 * encapsulating a <code>Cumulate</code> within a
 * <code>GroupProcessor</code>. Another way is illustrated by the
 * {@link CounterSingle} class.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class CounterGroup extends GroupProcessor
{
	public CounterGroup()
	{
		super(1, 1);
		TurnInto one = new TurnInto(1);
		Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(one, sum);
		associateInput(0, one, 0);
		associateOutput(0, sum, 0);
		addProcessors(one, sum);
	}
}
