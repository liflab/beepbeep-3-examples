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

public class SetValueSlider
{
  public static void main(String[] args) throws InterruptedException
  {
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
    
    WidgetSink ws = new WidgetSink(m_slider);
    Pushable p = ws.getPushableInput();
    for (int i = 10; i <= 100; i+= 10)
    {
      p.push(i);
      Thread.sleep(1000);
    }
  }
}
