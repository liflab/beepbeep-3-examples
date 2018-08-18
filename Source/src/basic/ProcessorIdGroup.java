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
package basic;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.tmf.Passthrough;

/**
 * Example showing how the IDs of processors behave when multiple
 * instances are created.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class ProcessorIdGroup 
{
  public static void main(String[] args)
  {
    /// We create a first group and look at the processor IDs
    MyGroup g1 = new MyGroup();
    System.out.println("ID of g1: " + g1.getId());
    System.out.println("ID inside g1: " + g1.getInstance().getId());
    ///
    //* We create a second group and look at the IDs. Note that they
    // are all different from the first instance.
    MyGroup g2 = new MyGroup();
    System.out.println("ID of g2: " + g2.getId());
    System.out.println("ID inside g2: " + g2.getInstance().getId());
    //*
  }
  
  //!
  public static class MyGroup extends GroupProcessor
  {
    /* Member field used to keep a reference to the instance of
     * Passthrough created for this group. */
    protected Passthrough pt;
    
    /* This processor simply encapsulates a single Passthrough
     * processor inside. */
    public MyGroup()
    {
      super(1, 1);
      pt = new Passthrough();
      addProcessor(pt);
      associateInput(0, pt, 0);
      associateOutput(0, pt, 0);
    }
    
    /* This method returns the instance of Passthrough that has
     * been created for this instance of MyGroup. */
    public Passthrough getInstance()
    {
      return pt;
    }
  }
  //!
}
