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
package voyager;

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.signal.Smoothen;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tmf.Splice;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Strings;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gral.Scatterplot;
import plots.BitmapJFrame;

/**
 * Detect planetary encounters of Voyager 2 by analyzing its distance from
 * the Earth.
 * <h4>About the data files</h4>
 * <p>This example requires you to first download some data files from the
 * Voyager 2 space probe; these files are publicly available on a NASA FTP
 * site:
 * <p>
 * <a href="ftp://spdf.gsfc.nasa.gov/pub/data/voyager/voyager2/merged/">ftp://spdf.gsfc.nasa.gov/pub/data/voyager/voyager2/merged/</a>
 * <p>
 * @author Sylvain Hallé
 */
public class RadialAcceleration 
{
	public static void main(String[] args)
	{
		int start_year = 1985, end_year = 1992;
		
		/* The Voyager data is split into multiple CSV files, one per civil
		 * year. Let us create one ReadLines processor for each file. */
		ReadLines[] readers = new ReadLines[end_year - start_year + 1];
		for (int y = start_year; y <= end_year; y++)
		{
			readers[y - start_year] = new ReadLines(RadialAcceleration.class.getResourceAsStream("data/vy2_" + y + ".asc"));
		}
		
		/* Pass this array of readers to the Splice processors, so that their
		 * contents be used as an uninterrupted stream of events. */
		Splice spl = new Splice(readers);

		/* The input files have hourly readings; keep only one reading per week. */
		CountDecimate cd = new CountDecimate(24 * 1);
		Connector.connect(spl, cd);
		
		/* The 1977 file has no data before week 31 or so (the launch date);
		 * let's ignore these first lines. */
		//Trim ignore_beginning = new Trim(39*7);
		Trim ignore_beginning = new Trim(39*7);
		Connector.connect(cd, ignore_beginning);
		
		/* Split the surviving lines into arrays with the TAB character. */
		ApplyFunction to_array = new ApplyFunction(new Strings.SplitString("\\s+"));
		Connector.connect(ignore_beginning, to_array);
		
		/* Fork this stream of arrays in three. */
		Fork fork = new Fork(2);
		Connector.connect(to_array, fork);
		
		/* From first stream, format date */
		ApplyFunction format_date = new ApplyFunction(new FunctionTree(FormatDate.instance, new FunctionTree(new NthElement(0), StreamVariable.X), new FunctionTree(new NthElement(1), StreamVariable.X)));
		Connector.connect(fork, 0, format_date, INPUT);
		
		/* From second stream, extract distance */
		ApplyFunction get_au1 = new ApplyFunction(new FunctionTree(Numbers.numberCast, new FunctionTree(new NthElement(3), StreamVariable.X)));
		Connector.connect(fork, 1, get_au1, INPUT);
		
		/* Compute rate of change of distance */
		Slope speed = new Slope();
		Connector.connect(get_au1, speed);
		
		/* Compute rate of change of speed (2nd derivative) */
		Slope acceleration = new Slope();
		Connector.connect(speed, acceleration);
		Smoothen smooth = new Smoothen(2);
		Connector.connect(acceleration, smooth);
		
		UpdateTableStream table = new UpdateTableStream("Date", "Acceleration");
		
		Connector.connect(format_date, OUTPUT, table, 0);
		Connector.connect(smooth, OUTPUT, table, 1);
		
		CountDecimate reduce_resolution = new CountDecimate(15);
		Connector.connect(table, reduce_resolution);
		Scatterplot plot = new Scatterplot();
		plot.withPoints(false);
		plot.setTitle("Planetary encounters of Voyager 2");
		plot.setCaption(Axis.X, "Days after launch");
		plot.setCaption(Axis.Y, "AU");
		DrawPlot draw = new DrawPlot(plot);
		Connector.connect(reduce_resolution, draw);
		Pump pump = new Pump();
		Connector.connect(draw, pump);
		BitmapJFrame window = new BitmapJFrame();
		Connector.connect(pump, window);
		window.start();
		pump.start();
	}


}
