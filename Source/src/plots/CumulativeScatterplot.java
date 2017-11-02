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

import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.LEFT;
import static ca.uqac.lif.cep.Connector.RIGHT;

import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTable;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.mtnp.plot.gral.Scatterplot;

/**
 * Update a 2D scatterplot in realtime from two streams of numbers.
 * 
 * @author Sylvain Hallé
 */
public class CumulativeScatterplot
{

	public static void main(String[] args) throws ConnectorException, InterruptedException
	{
		QueueSource one = new QueueSource();
		one.addEvent(1);
		RandomTwoD random = new RandomTwoD();
		Connector.connect(one, random);
		UpdateTable table = new UpdateTableStream("x", "y");
		Connector.connect(random, LEFT, table, LEFT);
		Connector.connect(random, RIGHT, table, RIGHT);
		
		/* Prepare a plot */
		DrawPlot draw = new DrawPlot(new Scatterplot());
		Connector.connect(table, draw);
		
		/* Show plot in a JFrame */
		BitmapJFrame window = new BitmapJFrame();
		Connector.connect(draw, window);
		window.start();
		
		System.out.println("Displaying plot. Press Ctrl+C or close the window to end.");
		while (true)
		{
			one.push();
			Thread.sleep(1000);
		}
	}
}
