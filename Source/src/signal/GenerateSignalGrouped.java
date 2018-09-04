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

public class GenerateSignalGrouped
{
  public static void main(String[] args)
  {
    GenerateSignal gs = new GenerateSignal(
        new Object[] {0, 10, -5, 0, -7, 0},
        new Object[] {5, 5, 3, 5, 5, 5});
    PlotSignal ps = new PlotSignal("T", "V");
    Connector.connect(gs, ps);
    ps.start();
  }  
}
