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
package customprocessors;

import java.util.Queue;

import ca.uqac.lif.cep.*;

public class OutIfPositive extends SynchronousProcessor {

	public OutIfPositive() {
		super(1, 1);
	}

	public boolean compute(Object[] inputs, Queue<Object[]> outputs) {
		Number n = (Number) inputs[0];
		if (n.floatValue() > 0)
			outputs.add(new Object[]{n});
		return true;
	}

	@Override
	public Processor duplicate(boolean with_state) {
		return new OutIfPositive();
	}
}