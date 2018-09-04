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
package widgets;

import plots.BitmapJFrame;
import util.UtilityMethods;
import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTable;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.widgets.GetWidgetValue;
import ca.uqac.lif.cep.widgets.ListenerSource;
import ca.uqac.lif.mtnp.plot.gral.Scatterplot;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

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
 * <li>{@link ca.uqac.lif.cep.functions.Cumulate Cumulate}</li>
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
	  /* Create a window with a slider */
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    JSlider m_slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
    m_slider.setMajorTickSpacing(20);
    m_slider.setPaintTicks(true);
    m_slider.setPaintLabels(true);
    JLabel slider_label = new JLabel("Value", JLabel.CENTER);
    slider_label.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(slider_label);
    panel.add(m_slider);
    panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    JFrame m_frame = new JFrame("My Widget Frame");
    m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    m_frame.add(panel);
    m_frame.pack();
    m_frame.setVisible(true);
    
    ListenerSource ls = new ListenerSource();
    m_slider.addChangeListener(ls);
		ApplyFunction gwv = new ApplyFunction(GetWidgetValue.instance);
		Connector.connect(ls, gwv);
		Fork fork = new Fork(3);
		Connector.connect(gwv, fork);
		TurnInto one = new TurnInto(1);
		Connector.connect(fork, 0, one, INPUT);
		Cumulate counter = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
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

		Processor.startAll(plot_frame);
		UtilityMethods.waitForever();
	}
	
	
}
