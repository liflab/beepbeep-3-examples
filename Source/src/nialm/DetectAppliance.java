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
package nialm;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.signal.Limit;
import ca.uqac.lif.cep.signal.PeakFinderLocalMaximum;
import ca.uqac.lif.cep.signal.PlateauFinder;
import ca.uqac.lif.cep.signal.Threshold;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.util.Bags;
import signal.GenerateSignalNoise;

public class DetectAppliance
{
  public static void main(String[] args)
  {
    ///
    Fork f = new Fork(2);
    // First branch: peak detection
    Processor peak_finder = new PeakFinderLocalMaximum(5);
    Connector.connect(f, 0, peak_finder, 0);
    // Threshold to avoid finding peaks due to noise
    Threshold peak_th = new Threshold(100);
    Connector.connect(peak_finder, peak_th);
    // Dampen to avoid double peaks
    Processor peak_damper = new Limit(10);
    Connector.connect(peak_th, peak_damper);
    
    // Second branch: plateau detection
    Processor plateau_finder = new PlateauFinder()
        .setPlateauRange(5).setRelative(true);
    Connector.connect(f, 1, plateau_finder, 0);
    // Threshold to avoid finding plateaus due to noise
    Threshold plateau_th = new Threshold(100);
    Connector.connect(plateau_finder, plateau_th);
    Processor plateau_damper = new Limit(10);
    Connector.connect(plateau_th, plateau_damper);
    ///
    
    //! Step 2: send to a Moore machine
    ApplianceMooreMachine amm = new ApplianceMooreMachine(1000, 700, -700, 150);
    Connector.connect(peak_damper, 0, amm, 0);
    Connector.connect(plateau_damper, 0, amm, 1);
    
    // Connect input to a fake signal to see how it works
    GenerateSignalNoise signal = new GenerateSignalNoise(10f,
        new Object[] {0, 333, -150, 0, -175, 0},
        new Object[] {70, 3, 2, 100, 4, 60});
    Connector.connect(signal, 1, f, 0);
    
    // Connect the output and print at the console
    ApplyFunction to_list = new ApplyFunction(new Bags.ToList(Number.class, Number.class));
    Connector.connect(signal, 0, to_list, 0);
    Connector.connect(amm, 0, to_list, 1);
    //!
    
    // Pull events
    Pullable p = to_list.getPullableOutput();
    while (p.hasNext())
    {
      System.out.println(p.pull());
    }
  }
}
