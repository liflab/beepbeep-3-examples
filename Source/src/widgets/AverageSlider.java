package widgets;

import plots.BitmapJFrame;
import util.UtilityMethods;
import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTable;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.mtnp.plot.gral.Scatterplot;

/**
 * Display a real-time plot of the values of a slider in a Swing frame.
 * In this example, a <tt>JFrame</tt> containing a simple slider widget
 * is polled for its value twice per second. This value is then processed
 * to display a 2D scatterplot with two data series:
 * <ul>
 * <li>The value of the slider at each time point</li>
 * <li>The average value of the slider for the last 4 time points</li> 
 * </ul>
 * Here is a screenshot of the two JFrames used in this example. At the left
 * is the slider the user can move, and at the right, the generated plot that
 * updates once per second. Notice how the use of an average (green line)
 * smoothes the jittery motion of the raw values (blue line).
 * <p>
 * <img src="{@docRoot}/doc-files/widgets/AverageSlider-screenshot.png" width="50%">
 * <p>
 * Graphically, the processor chain for this example can be represented
 * as follows:  
 * <p>
 * <img src="{@docRoot}/doc-files/widgets/AverageSlider.png" alt="Processor graph"></a>
 * <p>
 * As one can see, this example is notable for its mix of various processors:
 * <ul>
 * <li>{@link ca.uqac.lif.cep.tmp.CountDecimate CountDecimate}</li>
 * <li>{@link ca.uqac.lif.cep.functions.CumulativeProcessor CumulativeProcessor}</li>
 * <li>{@link ca.uqac.lif.cep.mtnp.DrawPlot DrawPlot}</li>
 * <li>{@link ca.uqac.lif.cep.tmf.Fork Fork}</li>
 * <li>{@link ca.uqac.lif.cep.functions.ApplyFunction FunctionProcessor}</li>
 * <li>{@link ca.uqac.lif.cep.GroupProcessor GroupProcessor}</li>
 * <li>{@link ca.uqac.lif.cep.tmf.Pump Pump}</li>
 * <li>{@link ca.uqac.lif.cep.tmf.Trim Trim}</li>
 * <li>{@link ca.uqac.lif.cep.mtnp.UpdateTable UpdateTable}</li>
 * <li>{@link ca.uqac.lif.cep.Window Window}</li>
 * </ul>
 * 
 * @author Sylvain Hallé
 * @difficulty Medium
 */
public class AverageSlider 
{
	public static void main(String[] args) throws ProcessorException
	{
		SliderFrame frame = new SliderFrame();
		Pump pump = new Pump(500);
		Connector.connect(frame, pump);
		Fork fork = new Fork(3);
		Connector.connect(pump, fork);
		ApplyFunction one = new ApplyFunction(new Constant(1));
		Connector.connect(fork, 0, one, INPUT);
		CumulativeProcessor counter = new CumulativeProcessor(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(one, counter);
		Trim trim1 = new Trim(4);
		Connector.connect(counter, trim1);
		Trim trim2 = new Trim(4);
		Connector.connect(fork, 1, trim2, INPUT);
		Window win = new Window(new Average(), 4);
		Connector.connect(fork, 2, win, INPUT);
		
		UpdateTable table = new UpdateTableStream("t", "x", "x̅");
		Connector.connect(counter, OUTPUT, table, 0);
		Connector.connect(trim2, OUTPUT, table, 1);
		Connector.connect(win, OUTPUT, table, 2);
		CountDecimate decimate = new CountDecimate(2);
		Connector.connect(table, decimate);

		DrawPlot plot = new DrawPlot(new Scatterplot());
		Connector.connect(decimate, plot);
		BitmapJFrame plot_frame = new BitmapJFrame();
		Connector.connect(plot, plot_frame);
		
		Processor.startAll(frame, pump, plot_frame);
		UtilityMethods.waitForever();
	}
	
	
}
