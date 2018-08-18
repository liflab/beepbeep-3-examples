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
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Illustration of the difference between using
 * {@link ca.uqac.lif.cep.Pullable#pull() pull()} and
 * {@link ca.uqac.lif.cep.Pullable#pullSoft() pullSoft()} on a
 * {@link ca.uqac.lif.cep.Pullable Pullable} object.
 * In this example, we use the "soft" way of fetching
 * objects from a Pullable.
 * 
 * @author Sylvain Hallé
 * @see PullHard
 */
public class PullSoft 
{
	public static void main(String[] args)
	{
		///
	  QueueSource source = new QueueSource().loop(false);
	  source.setEvents(0, 1, 2, 3);
	  CountDecimate decim = new CountDecimate(2);
	  Connector.connect(source, decim);
	  Pullable p = decim.getPullableOutput();
		///
	  //!
	  for (int i = 0; i < 5; i++)
	  {
	    Pullable.NextStatus s = p.hasNextSoft();
	    System.out.println(s);
	    if (s == Pullable.NextStatus.YES)
	    {
	      System.out.println(p.pullSoft());
	    }
	  }
	  //!
	}
}
