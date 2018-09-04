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
package signal;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.VariableStutter;
import ca.uqac.lif.cep.widgets.ToImageIcon;
import ca.uqac.lif.cep.widgets.WidgetSink;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.mtnp.plot.gral.Scatterplot;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FakeSignal
{

  public static void main(String[] args)
  {
    QueueSource values = new QueueSource();
    values.setEvents(0, 10, -5, 0, -7, 0);
    QueueSource numbers = new QueueSource();
    numbers.setEvents(5, 5, 3, 5, 5, 5);
    values.loop(false);
    VariableStutter vs = new VariableStutter();
    Connector.connect(values, 0, vs, 0);
    Connector.connect(numbers, 0, vs, 1);
    Fork fork = new Fork(2);
    Connector.connect(vs, fork);
    TurnInto one = new TurnInto(1);
    Connector.connect(fork, 0, one, 0);
    Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(one, sum_one);
    Cumulate sum_values = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(fork, 1, sum_values, 0);
    UpdateTableStream uts = new UpdateTableStream("T", "V");
    Connector.connect(sum_one, 0, uts, 0);
    Connector.connect(sum_values, 0, uts, 1);
    DrawPlot plot = new DrawPlot(new Scatterplot());
    Connector.connect(uts, plot);
    ApplyFunction to_icon = new ApplyFunction(ToImageIcon.instance);
    Connector.connect(plot, to_icon);
    Pump pump = new Pump();
    Connector.connect(to_icon, pump);
    
    // Create a frame to display the plot
    JFrame frame = new JFrame("My plot");
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    frame.add(panel);
    frame.setSize(800, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JLabel label = new JLabel();
    panel.add(label);
    WidgetSink sink = new WidgetSink(label);
    Connector.connect(pump, sink);
    frame.setVisible(true);
    pump.start();
  }
}
