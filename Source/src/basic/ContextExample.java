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
package basic;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.ContextVariable;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Use a processor's {@link ca.uqac.lif.cep.Context Context} object.
 * @author Sylvain Hallé
 */
public class ContextExample 
{
	public static void main(String[] args)
	{
		///
		ApplyFunction af = new ApplyFunction(new FunctionTree(
				Numbers.addition,
				StreamVariable.X,
				new ContextVariable("foo")));
		Connector.connect(af, new Print());
		af.setContext("foo", 10);
		Pushable p = af.getPushableInput();
		p.push(3);
		af.setContext("foo", 6);
		p.push(4);
		///
	}
}
