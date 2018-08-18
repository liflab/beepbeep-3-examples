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
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Usage of a processor's {@link ca.uqac.lif.cep.Processor#reset() reset()}
 * method.
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class ResetWindow
{
  public static void main(String[] args)
  {
    /// We create a window processor that will compute a sum of numbers
    // over a sliding window of width 3
    Window w = new Window(new Cumulate(
        new CumulativeFunction<Number>(Numbers.addition)), 3);
    Connector.connect(w, new Print());
    Pushable p = w.getPushableInput();
    // Let us push some events and print them
    p.push(3).push(1).push(4).push(1).push(6);
    // We now reset the window and push new events. Notice how the
    // first two calls on push do not print anything.
    w.reset();
    p.push(2).push(7).push(1).push(8).push(3);
    ///
  }

}
