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
 * This example is similar to {@link CumulativeScatterplot} (which you should
 * study first), except for three things:
 * <ul>
 * <li>The two outputs of the {@link RandomTwoD} processor are merged into
 * arrays of size 2 by the {@link NaryToArray} processor.</li>
 * <li>The creation of a table out of input streams is done with an
 * {@link UpdateTableArray} processor</li>
 * <li>The creation of a table out of input streams is encased in a
 * {@link Window} processor} of 100 events, and only one table every
 * 20 is pushed downstream.</li>
 * </ul>
 * Graphically, this chain of processor can be described as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/plots/WindowScatterplot.png" alt="Processor graph">
 * @author Sylvain Hallé
 */
public class WindowScatterplot
{
	public static void main(String[] args) throws ConnectorException, InterruptedException
	{
		/* Create a stream of random x-y pairs, as in
		 * {@link CumulativeScatterplot} */
		QueueSource one = new QueueSource();
		one.addEvent(1);
		RandomTwoD random = new RandomTwoD();
		Connector.connect(one, random);
		
		/* Merge the two streams into arrays of size 2. Each output event
		 * is an array with the x and the y value. Since random has an
		 * output arity of 2, and array_convert has an input arity of 2,
		 * we don't need to explicitly connect both input/output pairs. */
		NaryToArray array_convert = new NaryToArray(2);
		Connector.connect(random, array_convert);
		
		/* Use the UpdateTable processor that takes as input a single
		 * stream of arrays. */
		UpdateTableArray update_table = new UpdateTableArray("x", "y");
		
		/* We encase this processor in a window processor of width 100 */
		Window window = new Window(update_table, 100);
		
		/* It is this processor that we connect to array_convert */
		Connector.connect(array_convert, window);
		
		/* To slow down the refreshing of the plot, we keep only one
		 * table for every 20 output by the window processor. This is done
		 * with a CountDecimate processor. */
		CountDecimate decimate = new CountDecimate(20);
		Connector.connect(window, decimate);
		
		/* The plot, draw and display parts of this example are identical
		 * to {@link CumulativeScatterplot}. */
		Scatterplot plot = new Scatterplot();
		DrawPlot draw = new DrawPlot(plot);
		Connector.connect(decimate, draw);
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
