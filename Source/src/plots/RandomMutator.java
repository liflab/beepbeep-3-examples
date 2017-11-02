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

import java.util.Random;

import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.UniformProcessor;

/**
 * Processor that converts any input into a random integer. This
 * processor is represented graphically as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/plots/RandomMutator.png" alt="Processor">
 * @author Sylvain Hallé
 */
public class RandomMutator extends UniformProcessor
{
	/**
	 * A Random object
	 */
	protected Random m_random;
	
	/**
	 * The minimum random number that can be generated
	 */
	protected int m_minValue = 0;
	
	/**
	 * The minimum random number that can be generated
	 */
	protected int m_maxValue = 0;

	public RandomMutator(int min_value, int max_value)
	{
		super(1, 1);
		m_random = new Random();
		m_minValue = min_value;
		m_maxValue = max_value;
	}

	@Override
	protected boolean compute(Object[] inputs, Object[] outputs) throws ProcessorException
	{
		outputs[0] = m_random.nextInt(m_maxValue - m_minValue) + m_minValue;
		return true;
	}

	@Override
	public RandomMutator clone()
	{
		return new RandomMutator(m_minValue, m_maxValue);
	}
}
