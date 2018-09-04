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
package widgets;

import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Group processor that computes the cumulative average of a stream of
 * numbers. This processor is similar to the {@link basic.Average Average}
 * examples of the {@link basic} section. Graphically, it corresponds to the
 * following chain of processors:
 * <p>
 * <img
 *   src="{@docRoot}/doc-files/widgets/Average.png"
 *   alt="Processor graph">
 * <p>
 * Since we encapsulate this chain into a 
 * {@link ca.uqac.lif.cep.GroupProcessor GroupProcessor}, in other processor
 * graphs that use it, it will be drawn as this:
 * <p>
 * <img
 *   src="{@docRoot}/doc-files/widgets/Average-box.png"
 *   alt="Processor graph">
 * 
 * @author Sylvain Hallé
 * @see basic.Average Average
 * @difficulty Easy
 */
public class Average extends GroupProcessor 
{

	public Average()
	{
		super(1, 1);
		Fork fork = new Fork(2);
		associateInput(0, fork, 0);
		TurnInto one = new TurnInto(1);
		Connector.connect(fork, BOTTOM, one, INPUT);
		Cumulate count = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(one, count);
		Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(fork, TOP, sum, INPUT);
		ApplyFunction div = new ApplyFunction(Numbers.division);
		Connector.connect(sum, OUTPUT, div, TOP);
		Connector.connect(count, OUTPUT, div, BOTTOM);
		associateOutput(0, div, 0);
		addProcessors(fork, one, count, sum, div);
	}
}
