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

import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.widgets.WidgetSink;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * Set the value of a widget using a stream of events.
 */
public class SetValueSlider
{
  public static void main(String[] args) throws InterruptedException
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
    slider.setMajorTickSpacing(20);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    JLabel slider_label = new JLabel("Value", JLabel.CENTER);
    slider_label.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(slider_label);
    panel.add(slider);
    panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    JFrame frame = new JFrame("My Widget Frame");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setVisible(true);
    
    //!
    WidgetSink ws = new WidgetSink(slider);
    Pushable p = ws.getPushableInput();
    for (int i = 10; i <= 100; i+= 10)
    {
      p.push(i);
      Thread.sleep(1000);
    }
    //!
  }
}
