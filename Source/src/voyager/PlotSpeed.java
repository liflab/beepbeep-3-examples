package voyager;

import plots.BitmapJFrame;
import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.signal.Limiter;
import ca.uqac.lif.cep.signal.PeakFinderLocalMaximum;
import ca.uqac.lif.cep.signal.Smoothen;
import ca.uqac.lif.cep.signal.Threshold;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.KeepLast;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tmf.Splice;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Strings;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gral.Scatterplot;

/**
 * Detect planetary encounters of 
 * <a href="https://en.wikipedia.org/wiki/Voyager_2">Voyager 2</a> by analyzing
 * its distance from the Earth.
 * <p>
 * In this example, we read a processed data feed that provides averaged
 * hourly readings of various instruments in the spacecraft. We are
 * interested in the date and the relative distance to Earth, expressed in
 * astronomical units (AU). From these two data fields, we compute the
 * average weekly speed (in AU/week), and send the output stream to signal
 * processors that detect peaks (i.e. abrupt variations in the values of
 * a numerical signal). This is shown in a plot.
 * <p>
 * One can see that the last three peaks correspond precisely to the dates of
 * Voyager's flybys of Jupiter, Saturn, and Neptune:
 * <p>
 * <table>  
 * <tr><td>Planet</td><td>Date</td><td>Days after 77/1/1</td></tr>
 * <tr><td>Jupiter</td><td>July 9, 1979</td><td>918</td></tr>
 * <tr><td>Saturn</td><td>August 25, 1981</td><td>1696</td></tr>
 * <tr><td>Neptune</td><td>August 25, 1989</td><td>4618</td></tr>
 * </table>
 * <p>
 * The flyby of Uranus (Jaunary 24, 1986, or Day 3310) does not produce
 * a speed variation large enough to be detected through this method.
 * 
 * <h4>About the data files</h4>
 * <p>This example requires you to first download some data files from the
 * Voyager 2 space probe; these files are publicly available on a NASA FTP
 * site:
 * <p>
 * <a href="ftp://spdf.gsfc.nasa.gov/pub/data/voyager/voyager2/merged/">ftp://spdf.gsfc.nasa.gov/pub/data/voyager/voyager2/merged/</a>
 * <p>
 * @author Sylvain Hallé
 */
public class PlotSpeed 
{
	public static void main(String[] args)
	{
		int start_year = 1977, end_year = 1992;
		
		/* The Voyager data is split into multiple CSV files, one per civil
		 * year. Let us create one ReadLines processor for each file. */
		ReadLines[] readers = new ReadLines[end_year - start_year + 1];
		for (int y = start_year; y <= end_year; y++)
		{
			readers[y - start_year] = new ReadLines(PlotSpeed.class.getResourceAsStream("data/vy2_" + y + ".asc"));
		}
		
		/* Pass this array of readers to the Splice processors, so that their
		 * contents be used as an uninterrupted stream of events. */
		Splice spl = new Splice(readers);

		/* The input files have hourly readings; keep only one reading per week. */
		CountDecimate cd = new CountDecimate(24 * 7);
		Connector.connect(spl, cd);
		
		/* The 1977 file has no data before week 31 or so (the launch date);
		 * let's ignore these first lines. */
		Trim ignore_beginning = new Trim(31);
		Connector.connect(cd, ignore_beginning);
		
		/* Split the surviving lines into arrays with the TAB character. */
		ApplyFunction to_array = new ApplyFunction(new Strings.SplitString("\\s+"));
		Connector.connect(ignore_beginning, to_array);
		
		/* Fork this stream of arrays in three. */
		Fork fork = new Fork(3);
		Connector.connect(to_array, fork);
		
		/* From first stream, format date */
		ApplyFunction format_date = new ApplyFunction(new FunctionTree(FormatDate.instance, new FunctionTree(new NthElement(0), StreamVariable.X), new FunctionTree(new NthElement(1), StreamVariable.X)));
		Connector.connect(fork, 0, format_date, INPUT);
		
		/* From second stream, extract distance */
		ApplyFunction get_au1 = new ApplyFunction(new FunctionTree(ToSciNumber.instance, new FunctionTree(new NthElement(3), StreamVariable.X)));
		Connector.connect(fork, 1, get_au1, INPUT);
		
		/* Delay third stream by one reading, and extract distance */
		Trim cd_delay = new Trim(1);
		Connector.connect(fork, 2, cd_delay, INPUT);
		ApplyFunction get_au2 = new ApplyFunction(new FunctionTree(ToSciNumber.instance, new FunctionTree(new NthElement(3), StreamVariable.X)));
		Connector.connect(cd_delay, get_au2);
		
		/* Subtract third and second streams, giving the weekly distance
		 * travelled. */
		ApplyFunction distance = new ApplyFunction(new FunctionTree(Numbers.maximum, Constant.ZERO, new FunctionTree(Numbers.subtraction, StreamVariable.X, StreamVariable.Y)));
		Connector.connect(get_au2, OUTPUT, distance, TOP);
		Connector.connect(get_au1, OUTPUT, distance, BOTTOM);
		
		/* Since the weekly distance is very close to the measurement's
		 * precision, smoothen those values by averaging each two successive
		 * points. */
		Smoothen smooth = new Smoothen(3);
		Connector.connect(distance, smooth);
		
		Fork f2 = new Fork(2);
		Connector.connect(smooth, f2);
		PeakFinderLocalMaximum peak = new PeakFinderLocalMaximum(5);
		//Passthrough peak = new Passthrough();
		Connector.connect(f2, BOTTOM, peak, INPUT);
		Threshold th = new Threshold(0.0125f);
		Connector.connect(peak, th);
		Limiter li = new Limiter(5);
		Connector.connect(th, li);
		
		UpdateTableStream table = new UpdateTableStream("Date", "Speed (AU/week)", "Peak");
		
		Connector.connect(format_date, OUTPUT, table, 0);
		Connector.connect(f2, OUTPUT, table, 1);
		Connector.connect(li, OUTPUT, table, 2);
		
		Pump pump = new Pump();
		Connector.connect(table, pump);
		
		KeepLast last = new KeepLast(1);
		Connector.connect(pump, last);
		Scatterplot plot = new Scatterplot();
		plot.setTitle("Planetary encounters of Voyager 2");
		plot.setCaption(Axis.X, "Days after 1/1/77");
		plot.setCaption(Axis.Y, "AU");
		DrawPlot draw = new DrawPlot(plot);
		Connector.connect(last, draw);
		
		BitmapJFrame window = new BitmapJFrame();
		Connector.connect(draw, window);
		window.start();
		pump.start();
	}
}
