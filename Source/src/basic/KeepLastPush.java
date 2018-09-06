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
package basic;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.KeepLast;

/**
 * Push events to the {@link ca.uqac.lif.cep.tmf.KeepLast KeepLast} processor.
 * The chain of processors in this example can be represented
 * graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/KeepLastPush.png" alt="Processor graph">
 * <p>
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class KeepLastPush
{
	public static void main(String[] args)
	{
	  ///
		KeepLast kl = new KeepLast();
		Print print = new Print();
		Connector.connect(kl, print);
		Pushable p = kl.getPushableInput();
		p.push(1);
		p.push(2);
		p.push(3);
		p.notifyEndOfTrace();
		///
	}
}