/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2020 Sylvain Hallé

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

import javax.swing.JFrame;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.mtnp.DrawPlot;
import ca.uqac.lif.cep.mtnp.UpdateTableArray;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.widgets.ListenerSource;
import ca.uqac.lif.cep.widgets.MouseCoordinates;
import ca.uqac.lif.mtnp.plot.gnuplot.HeatMap;
import ca.uqac.lif.mtnp.table.FrequencyTable;

/**
 * Displays the trail of the mouse pointer inside a heat map. The program
 * creates two windows: the first is the "mouse playground", which is a small
 * empty frame where the mouse movements are recorded; the second is a
 * window that displays a "heat map", which is a two-dimensional plot made
 * of rectangular cells, where the color of each cell is associated to a
 * numerical value. In the present case, each cell represents a rectangular
 * zone of pixels in the mouse playground, and the numerical value for each
 * zone is the total number of mouse events that occurred in that zone.
 * The end result looks like the following screenshot:
 * <p>
 * <img src="{@docRoot}/doc-files/plots/heatmap.png" alt="Screenshot" />
 * <p>
 * One can see the heatmap update as the user moves the mouse in the
 * playground. Colors that tend towards blue indicate that the mouse has not
 * spent a lot of time in that zone, while red and yellow colors indicate
 * the mouse has spent more time. The plot only updates when the mouse moves.
 */
public class MouseHeatmap
{
  /**
   * The width of the mouse playground
   */
  protected static int WIDTH = 320;
  
  /**
   * The height of the mouse playground
   */
  protected static int HEIGHT = 200;
  
  public static void main(String[] args) throws InterruptedException
  {
    /* Create a window where mouse movements will be trapped by
     * a listener source. */
    JFrame mouse_playground = new JFrame();
    mouse_playground.setSize(WIDTH, HEIGHT);
    mouse_playground.setTitle("Mouse playground");
    mouse_playground.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ListenerSource listener = new ListenerSource();
    mouse_playground.addMouseMotionListener(listener);
    mouse_playground.setVisible(true);
    
    /* Extract from each mouse event its x-y coordinates. */
    ApplyFunction coords = new ApplyFunction(MouseCoordinates.instance);
    Connector.connect(listener, coords);
    
    /* Use the UpdateTable processor that takes as input a single
     * stream of arrays. */
    UpdateTableArray update_table = new UpdateTableArray(
        new FrequencyTable(0, WIDTH, 10, 0, HEIGHT, 10, 1d));

    /* We encase this processor in a window processor of width 100 */
    Window window = new Window(update_table, 100);
    Connector.connect(coords, window);

    /* To slow down the refreshing of the plot, we keep only one
     * table for every 20 output by the window processor. This is done
     * with a CountDecimate processor. */
    CountDecimate decimate = new CountDecimate(20);
    Connector.connect(window, decimate);

    /* Draw a heat map out of the coordinates. */
    HeatMap plot = new HeatMap();
    plot.setTitle("Heat map");
    DrawPlot draw = new DrawPlot(plot);
    Connector.connect(decimate, draw);
    BitmapJFrame frame = new BitmapJFrame();
    frame.getFrame().setLocationRelativeTo(null);
    Connector.connect(draw, frame);
    System.out.println("Move your mouse in the window called \"Mouse playground\".");
    System.out.println("The heatmap will appear in the other window.");
    System.out.println("Press Ctrl+C or close the window to end.");
    
    /* Show the frame. */
    frame.start();
    
  }
}
