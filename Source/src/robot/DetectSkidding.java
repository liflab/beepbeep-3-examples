/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2023 Sylvain Hallé

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
package robot;

import static ca.uqac.lif.cep.Connector.connect;

import java.util.Vector;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.complex.RangeCep;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IfThenElse;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.io.Print.Println;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Freeze;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Detects situations where a moving robot loses traction and starts skidding.
 * For the purpose of this example, skidding is is identified as a prolonged
 * interval during which the orientation of the robot differs significantly
 * from the orientation of its movement (i.e. the robot is pointing in one
 * direction but moving in another one).
 * <p>
 * The example does not merely detect a skidding interval (which would be a
 * relatively classical case of runtime verification). Rather, it summarizes
 * a skidding situation by outputting a complex event called a "skidding
 * incident", which is only emitted once the skidding ends. This event is a
 * triplet containing:
 * <ul>
 * <li>The modulus of the robot's speed vector at the start of the skidding
 * interval (i.e. the initial speed when the skidding started)</li>
 * <li>The number of consecutive events the skidding incident is spanning</li>
 * <li>The maximum deviation observed between the robot's orientation and
 * its direction of motion during the skidding incident</li>
 * </ul>
 * This example makes use of the {@link RangeCep} processor, which can create
 * "complex" events summarizing a stream of lower-level events.
 * <p>
 * To make the example more realistic, the detection of skidding allows a
 * few "normal" events to occur interspersed with skidding events. The
 * condition is that <i>m</i> out of <i>n</i> successive events should reveal
 * a skidding state for the overall sequence be considered as an occurrence of
 * skidding.  
 * 
 * @author Sylvain Hallé
 */
public class DetectSkidding
{

  @SuppressWarnings("unused")
  public static void main(String[] args)
  {

    /* The angle (in radians) between the orientation and velocity over which
     * the robot is considered as skidding. We arbitrarily use 30 degrees. */
    float skid_angle = rad(30);

    /* The width of the window (n) and the minimum number of skidding events
     * in the window (m) required to recognize a skidding situation. */
    int m = 2, n = 3;
    
    /* Create a function that measures the angular deviation between the
     * robot's orientation and the velocity vector. */
    FunctionTree angular_dev = new FunctionTree(Numbers.absoluteValue, 
        new FunctionTree(Numbers.subtraction, 
            new FunctionTree(RobotEvent.getAngle, StreamVariable.X),
            new FunctionTree(ArcTanVector.instance, new FunctionTree(RobotEvent.getVel, StreamVariable.X))));

    /* Create a function that checks if a robot event represents a skidding
     * state. This is the case when the difference between the robot's
     * orientation and the angle of its velocity vector exceed the skid angle
     * defined above. */
    FunctionTree skidding = new FunctionTree(Numbers.isGreaterOrEqual,
        angular_dev.duplicate(), new Constant(skid_angle));

    /* Create a processor that checks if a sequence of events represents the
     * start of a skidding situation. This happens when the robot is in a
     * skidding state for m out of n successive events.
     */
    GroupProcessor skid_start = new GroupProcessor(1, 1);
    {
      Window win = new Window(new GroupProcessor(1, 1) {{
          Fork f = new Fork(3);
          ApplyFunction eq = new ApplyFunction(skidding);
          connect(f, 0, eq, 0);
          ApplyFunction ite = new ApplyFunction(IfThenElse.instance);
          connect(eq, 0, ite, 0);
          TurnInto one = new TurnInto(1);
          connect(f, 1, one, 0);
          connect(one, 0, ite, 1);
          TurnInto zero = new TurnInto(0);
          connect(f, 2, zero, 0);
          connect(zero, 0, ite, 2);
          Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
          connect(ite, sum);
          addProcessors(f, eq, ite, one, zero, sum).associateInput(f).associateOutput(sum);
      }}, n);
      ApplyFunction gt = new ApplyFunction(new FunctionTree(Numbers.isGreaterOrEqual, StreamVariable.X, new Constant(m)));
      connect(win, gt);
      skid_start.addProcessors(win, gt).associateInput(win).associateOutput(gt);
    }

    /* Create a processor that checks if a sequence of events represents the
     * end of a skidding situation. For simplicity we take it as the negation
     * of the start condition. */
    GroupProcessor skid_end = new GroupProcessor(1, 1) {{
        Processor cond = skid_start.duplicate();
        ApplyFunction not = new ApplyFunction(Booleans.not);
        connect(cond, not);
        addProcessors(not, cond).associateInput(cond).associateOutput(not);
    }};

    /* Create a processor that gets the module of the velocity vector at the
     * start of a skidding incident. */
    GroupProcessor start_vel = new GroupProcessor(1, 1) {{
        ApplyFunction get_vel = new ApplyFunction(RobotEvent.getVel);
        Freeze f = new Freeze();
        connect(get_vel, f);
        addProcessors(get_vel, f).associateInput(get_vel).associateOutput(f);
    }};

    /* Create a processor that counts events in the skidding incident. */
    GroupProcessor ev_count = new GroupProcessor(1, 1) {{
        TurnInto one = new TurnInto(1);
        Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
        connect(one, sum);
        addProcessors(one, sum).associateInput(one).associateOutput(sum);
    }};

    /* Create a processor that calculates the maximum angular deviation. */
    GroupProcessor max_angle = new GroupProcessor(1, 1) {{
      ApplyFunction dev = new ApplyFunction(angular_dev.duplicate());
      Cumulate max = new Cumulate(new CumulativeFunction<Number>(Numbers.maximum));
      connect(dev, max);
      addProcessors(dev, max).associateInput(dev).associateOutput(max);
    }};
    
    /* Create a CEP processor that creates sliding events out of robot
     * events. */
    RangeCep cep = new RangeCep(skid_start,
        new Processor[] {start_vel, ev_count, max_angle},
        new Bags.ToList(Number.class, Number.class, Number.class));
    connect(cep, new Println());
    
    /* Push a few robot events to the pipeline to witness the detection of a
     * skidding incident. Normally the pipeline would be connected to a source
     * of robot events (such as a simulator) instead of manually pushing events
     * like what is done here. */
    RobotEvent e_s = new RobotEvent(vecp(0, 0), vecp(1, rad(45)), rad(15)); // skidding
    RobotEvent e_n = new RobotEvent(vecp(0, 0), vecp(1, rad(45)), rad(45)); // normal
    Pushable p = cep.getPushableInput();
    p.push(e_s);
    p.push(e_n);
    p.push(e_n);
    p.push(e_s);
    p.push(e_s);
    p.push(e_n);
    p.push(e_n);
  }

  /**
   * Utility method to create a 2D vector from Cartesian coordinates.
   * @param x The first coordinate
   * @param y The second coordinate
   * @return The vector
   */
  /*@ non_null @*/ public static Vector<Float> vecc(Number x, Number y)
  {
    Vector<Float> v = new Vector<Float>(2);
    v.add(x.floatValue());
    v.add(y.floatValue());
    return v;
  }
  
  /**
   * Utility method to create a 2D vector from polar coordinates.
   * @param r The modulus
   * @param theta The angle (in radians)
   * @return The vector
   */
  /*@ non_null @*/ public static Vector<Float> vecp(Number r, Number theta)
  {
    Vector<Float> v = new Vector<Float>(2);
    v.add(r.floatValue() * (float) Math.cos(theta.floatValue()));
    v.add(r.floatValue() * (float) Math.sin(theta.floatValue()));
    return v;
  }
  
  /**
   * Converts degrees to radians.
   * @param deg The angle in degrees
   * @return The angle in radians
   */
  public static float rad(float deg)
  {
    return (float) (2 * Math.PI * deg / 360f);
  }
}
