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

import java.util.Queue;

import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.tmf.Sink;

/**
 * Receives a byte array as an input, and shows it in a Swing
 * window as a picture. This processor is a sink: it receives
 * input events, but emits no output events. In the examples of this
 * package, it is represented graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/plots/BitmapJFrame.png" alt="Processor">
 * 
 * @author Sylvain Hallé
 */
public class BitmapJFrame extends Sink
{
	protected transient JFrame m_frame;

	protected transient JLabel m_label;

	public BitmapJFrame()
	{
		super(1);
		m_frame = new JFrame("My plot");
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		m_frame.add(panel);
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_label = new JLabel();
		panel.add(m_label);
	}

	@Override
	public void start()
	{
		m_frame.setSize(800, 600);
		m_frame.setVisible(true);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) throws ProcessorException 
	{
		byte[] bytes = (byte[]) inputs[0];
		m_label.setIcon(new ImageIcon(bytes));
		return false;
	}
	
	/**
	 * Gets the frame associated to the object
	 * @return The frame
	 */
	public JFrame getFrame()
	{
	  return m_frame;
	}

	@Override
	public Processor duplicate(boolean with_state) 
	{
		// Don't care
		return null;
	}
}