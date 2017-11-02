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

import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTableArray;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.NaryToArray;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.mtnp.plot.gral.Scatterplot;

/**
 * Update a windowed 2D scatterplot in realtime from two streams of numbers.
 * 
 * @author Sylvain Hallé
 */
public class WindowScatterplot
{
	public static void main(String[] args) throws ConnectorException, InterruptedException
	{
		QueueSource one = new QueueSource();
		one.addEvent(1);
		RandomTwoD random = new RandomTwoD();
		Connector.connect(one, random);
		NaryToArray array_convert = new NaryToArray(2);
		Connector.connect(random, array_convert);
		
		/* Apply a sliding window */
		Window window = new Window(new UpdateTableArray("x", "y"), 100);
		Connector.connect(array_convert, window);
		CountDecimate decimate = new CountDecimate(20);
		Connector.connect(window, decimate);
		
		/* Prepare a plot */
		Scatterplot plot = new Scatterplot();
		
		DrawPlot draw = new DrawPlot(plot);
		Connector.connect(decimate, draw);
		
		/* Show plot in a JFrame */
		BitmapJFrame frame = new BitmapJFrame();
		Connector.connect(draw, frame);
		frame.start();
		
		System.out.println("Displaying plot. Press Ctrl+C or close the window to end.");
		while (true)
		{
			one.push();
			Thread.sleep(10);
		}
	}
}
