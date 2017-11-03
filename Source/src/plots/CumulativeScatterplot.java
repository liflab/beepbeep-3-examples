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
import static ca.uqac.lif.cep.Connector.TOP;
import static ca.uqac.lif.cep.Connector.BOTTOM;

import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTable;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.mtnp.plot.gral.Scatterplot;

/**
 * Update a 2D scatterplot in realtime from two streams of numbers.
 * This example generates dummy (x,y) pairs of numbers, accumulates them
 * into a table, and shows a scatterplot of those numbers in a window
 * such as this one:
 * <p>
 * <img src="{@docRoot}/doc-files/plots/window-plot.png" alt="Processor graph" width="400">  
 * <p> 
 * Graphically, this chain of processor can be described as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/plots/CumulativeScatterplot.png" alt="Processor graph">
 * @author Sylvain Hallé
 */
public class CumulativeScatterplot
{

	public static void main(String[] args) throws ConnectorException, InterruptedException
	{
		/* A stream of (x,y) pairs is first created,
		 * with x an incrementing integer, and y a randomly selected number
		 * (see {@link RandomTwoD}).
		 * */
		QueueSource one = new QueueSource();
		one.addEvent(1);
		RandomTwoD random = new RandomTwoD();
		Connector.connect(one, random);

		/* The resulting x-stream and y-streams are then pushed into an
		 * {@link UpdateTableStream} processor. We instantiate this processor,
		 * telling it to create an empty {@link Table} object with two
		 * columns, called "x" and "y". */
		UpdateTable table = new UpdateTableStream("x", "y");
		
		/* This creates a processor with two input streams, one for the "x"
		 * values, and the other for the "y" values. Each pair of values from
		 * the x and y streams is used to append a new line to the (initially
		 * empty) table. We connect the two
		 * outputs of the random processor to these two inputs. */
		Connector.connect(random, TOP, table, TOP);
		Connector.connect(random, BOTTOM, table, BOTTOM);

		/* The next step is to create a plot out of the table's content.
		 * The {@link DrawPlot} processor receives a Table and passes it to
		 * a {@link ca.uqac.lif.mtnp.Plot Plot} object from the MTNP
		 * library. In our case, we want to create a scatterplot from the
		 * table's contents. */
		DrawPlot draw = new DrawPlot(new Scatterplot());
		Connector.connect(table, draw);

		/* Each event that comes out of the DrawPlot processor is an array
		 * of bytes corresponding to a bitmap image. To display that image,
		 * we use the BitmapJFrame processor, which opens a window and
		 * displays the image inside. */
		BitmapJFrame window = new BitmapJFrame();
		Connector.connect(draw, window);
		
		/* We need to call the start() method so that the window becomes
		 * visible. */
		window.start();

		/* All set! We'll now repeatedly tell the source to push events
		 * out. You should see the window being updated every second with
		 * more and more data points.
		 */
		System.out.println("Displaying plot. Press Ctrl+C or close the window to end.");
		while (true)
		{
			one.push();
			Thread.sleep(1000); // Sleep a little so the graph does not update too fast
		}
	}
}
