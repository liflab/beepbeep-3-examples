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
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.widgets.ToImageIcon;
import ca.uqac.lif.cep.widgets.WidgetSink;
import ca.uqac.lif.mtnp.plot.gral.Scatterplot;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlotSignal extends GroupProcessor
{
  protected Pump m_pump;
  
  protected JFrame m_frame;
  
  public PlotSignal(String ... col_names)
  {
    super(1, 0);
    UpdateTableStream uts = new UpdateTableStream(col_names);
    for (int i = 0; i < col_names.length; i++)
    {
      associateInput(i, uts, i);
    }
    DrawPlot plot = new DrawPlot(new Scatterplot());
    Connector.connect(uts, plot);
    ApplyFunction to_icon = new ApplyFunction(ToImageIcon.instance);
    Connector.connect(plot, to_icon);
    m_pump = new Pump();
    Connector.connect(to_icon, m_pump);
    
    // Create a frame to display the plot
    m_frame = new JFrame("My plot");
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    m_frame.add(panel);
    m_frame.setSize(800, 600);
    m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JLabel label = new JLabel();
    panel.add(label);
    WidgetSink sink = new WidgetSink(label);
    Connector.connect(m_pump, sink);
    addProcessors(uts, plot, to_icon, m_pump, sink);
  }
  
  @Override
  public void start()
  {
    m_frame.setVisible(true);
    m_pump.start();
  }
}
