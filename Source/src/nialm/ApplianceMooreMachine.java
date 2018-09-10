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

import ca.uqac.lif.cep.fsm.FunctionTransition;
import ca.uqac.lif.cep.fsm.MooreMachine;
import ca.uqac.lif.cep.fsm.TransitionOtherwise;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.util.Numbers;

public class ApplianceMooreMachine extends MooreMachine
{
  //State names; this is just to improve readability
  private static final int ST_0 = 0;
  private static final int ST_1 = 1;
  private static final int ST_2 = 2;
  private static final int ST_3 = 3;
  private static final int ST_4 = 4;

  public ApplianceMooreMachine(float peak, float plateau, float drop, float interval)
  {
    super(2, 1);
    // Create transition relation
    addTransition(ST_0, new FunctionTransition(// in state 0, event = peak, go to state 1
        withinRange(0, peak, interval),
        ST_1));
    addTransition(ST_0,
        // in state 0, event = otherwise, go to state 0
        new TransitionOtherwise(ST_0));
    addTransition(ST_1, new FunctionTransition(// in state 1, event = plateau, go to state 2
        withinRange(1, plateau, interval),
        ST_2));
    addTransition(ST_1,
        // in state 1, event = otherwise, go to state 1
        new TransitionOtherwise(ST_1));
    addTransition(ST_2, new FunctionTransition(// in state 2, event = drop, go to state 3
        withinRange(1, drop, interval),
        ST_3));
    addTransition(ST_2,
        // in state 2, event = otherwise, go to state 4
        new TransitionOtherwise(ST_4));
    addTransition(ST_3, new FunctionTransition(// in state 3, event = peak, go to state 1
        withinRange(0, peak, interval),
        ST_1));
    addTransition(ST_3,
        // in state 3, event = otherwise, go to state 0
        new TransitionOtherwise(ST_0));
    addTransition(ST_4, new FunctionTransition(// in state 4, event = drop, go to state 3
        withinRange(1, drop, interval),
        ST_3));
    addTransition(ST_4,
        // in state 4, event = otherwise, go to state 4
        new TransitionOtherwise(ST_4));
    // Add symbols to some states
    addSymbol(ST_0, new Constant("No change"));
    addSymbol(ST_1, new Constant("No change"));
    addSymbol(ST_2, new Constant("Appliance ON"));
    addSymbol(ST_3, new Constant ("Appliance OFF"));
    addSymbol(ST_4, new Constant("No change"));
  }
  
  /**
   * Generate a transition expressing the fact that a value is within a range
   * @param c The component to look at (0=peak, 1=plateau)
   * @param value The value
   * @param interval The half-width of the range
   * @return The condition
   */
  private static Function withinRange(int component, float value, float interval)
  {
    StreamVariable var = StreamVariable.X;
    if (component == 1)
    {
      var = StreamVariable.Y;
    }
    FunctionTree f = new FunctionTree(Numbers.isLessThan,
        new FunctionTree(Numbers.absoluteValue,
            new FunctionTree(Numbers.subtraction,
                var,
                new Constant(value))),
        new Constant(interval));
    return f;
  }
}
