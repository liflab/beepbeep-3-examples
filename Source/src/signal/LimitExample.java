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
package signal;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.signal.Limit;
import ca.uqac.lif.cep.tmf.Fork;

public class LimitExample
{
  public static void main(String[] args)
  {
    GenerateSignal gs = new GenerateSignal(
        new Object[] {0, 9, -2, -9, 7, 1, -8, 2},
        new Object[] {5, 1, 2,  1, 1, 1,  1, 5});
    Fork fork = new Fork(2);
    Connector.connect(gs, 1, fork, 0);
    Limit limit = new Limit(4);
    Connector.connect(fork, 1, limit, 0);
    PlotSignal ps = new PlotSignal("T", "V", "P");
    Connector.connect(gs, 0, ps, 0);
    Connector.connect(fork, 0, ps, 1);
    Connector.connect(limit, 0, ps, 2);
    ps.start();
  }  
}
