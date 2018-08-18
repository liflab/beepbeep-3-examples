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

import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Example showing how to access the unique ID of a processor.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class ProcessorId 
{
  public static void main(String[] args)
  {
    ///
    QueueSource source = new QueueSource().loop(false);
    source.setEvents(0, 1, 2, 3);
    CountDecimate decim = new CountDecimate(2);
    System.out.println("ID of source: " + source.getId());
    System.out.println("ID of decim: " + decim.getId());
    ///
  }
}
