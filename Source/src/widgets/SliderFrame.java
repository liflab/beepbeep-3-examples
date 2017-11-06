package widgets;

import java.awt.Component;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.tmf.Source;

/**
 * {@link javax.swing.JFrame JFrame} with a numerical slider that can
 * be queried for its value. This window is used in a couple of examples
 * in this section, such as {@link AverageSlider}. The <tt>SliderFrame</tt>
 * is implemented as a {@link ca.uqac.lif.cep.tmf.Source Source}
 * processor that has a <tt>JFrame</tt> as a member field. When asked for
 * a new event (through method {@link #compute(Object[], Queue) compute()}),
 * this processor returns a single integer corresponding to the current value
 * of the slider.
 * <p>
 * The window looks like this:
 * <p>
 * <a href="{@docRoot}/doc-files/widgets/SliderWindow-frame.png"><img
 *   src="{@docRoot}/doc-files/widgets/SliderWindow-frame.png"
 *   alt="Processor graph"></a>
 * <p>
 * Graphically, this processor is represented as:
 * <p>
 * <a href="{@docRoot}/doc-files/widgets/SliderFrame.png"><img
 *   src="{@docRoot}/doc-files/widgets/SliderFrame.png"
 *   alt="Processor graph"></a>
 * <p>
 * Since <tt>SliderFrame</tt> is a {@link ca.uqac.lif.cep.Processor Processor}
 * object, it can be connected to a processor chain just like any other
 * {@link ca.uqac.lif.cep.tmf.Source Source} processor.
 * @author Sylvain Hallé
 *
 */
public class SliderFrame extends Source
{
	/**
	 * The JFrame corresponding to the window to be displayed
	 */
	JFrame m_frame;

	/**
	 * The slider whose value we will query
	 */
	JSlider m_slider;

	/**
	 * Creates a new widget frame processor
	 */
	public SliderFrame()
	{
		super(1);
		/* Create a window with a slider */
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		m_slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
		m_slider.setMajorTickSpacing(20);
		m_slider.setPaintTicks(true);
		m_slider.setPaintLabels(true);
		JLabel slider_label = new JLabel("Value", JLabel.CENTER);
		slider_label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(slider_label);
		panel.add(m_slider);
		panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		m_frame = new JFrame("My Widget Frame");
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_frame.add(panel);
		m_frame.pack();
	}
	
	@Override
	public void start()
	{
		m_frame.setVisible(true);
	}
	
	@Override
	public void stop()
	{
		m_frame.setVisible(false);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException 
	{
		outputs.add(new Object[]{m_slider.getValue()});
		return true;
	}

	@Override
	public Processor clone()
	{
		// Don't care for this example
		return null;
	}
}