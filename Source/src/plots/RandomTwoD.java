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
package plots;

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.TOP;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.BOTTOM;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.tmf.Fork;

/**
 * Generates a random stream of x-y pairs. This processor internally forks
 * an input of stream of numbers. The first fork is left as is and
 * becomes the first output stream. The second fork is sent through a
 * {@link RandomMutator} and becomes the second output stream.
 * <p>
 * Graphically, this can be described as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/plots/RandomTwoD.png" alt="Processor graph">
 * <p>
 * When used in other processor chains, this group processor will be
 * represented as:
 * <p>
 * <img src="{@docRoot}/doc-files/plots/RandomTwoD-box.png" alt="Processor graph">
 * 
 * @author Sylvain Hallé
 */
public class RandomTwoD extends GroupProcessor 
{
	public RandomTwoD() 
	{
		super(1, 2);
		Fork fork = new Fork(2);
		associateInput(INPUT, fork, INPUT);
		CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(fork, TOP, sum, INPUT);
		RandomMutator random = new RandomMutator(0, 100);
		Connector.connect(fork, BOTTOM, random, INPUT);
		associateOutput(TOP, sum, OUTPUT);
		associateOutput(BOTTOM, random, OUTPUT);
		addProcessors(fork, sum, random);
	}
}
