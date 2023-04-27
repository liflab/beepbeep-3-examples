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

import java.util.Vector;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Event representing the status of a fictional robot that moves in a
 * two-dimensional space.
 * @author Sylvain Hallé
 */
public class RobotEvent
{
  /**
   * Static reference to the function {@link GetPosition}.
   */
  /*@ non_null @*/ public static final GetPosition getPos = new GetPosition();
  
  /**
   * Static reference to the function {@link GetVelocity}.
   */
  /*@ non_null @*/ public static final GetVelocity getVel = new GetVelocity();
  
  /**
   * Static reference to the function {@link GetAngle}.
   */
  /*@ non_null @*/ public static final GetAngle getAngle = new GetAngle();
  
  /**
   * A vector indicating the position of the robot.
   */
  protected final Vector<Float> m_position;
  
  /**
   * A vector indicating the velocity of the robot.
   */
  protected final Vector<Float> m_velocity;

  /**
   * The the angle of orientation of the robot (in radians).
   */
  protected final float m_theta;
  
  /**
   * Creates a new robot event.
   * @param position The position of the robot
   * @param velocity The velocity of the robot
   * @param theta The angle of orientation of the robot (in radians)
   */
  public RobotEvent(Vector<Float> position, Vector<Float> velocity, float theta)
  {
    super();
    m_position = position;
    m_velocity = velocity;
    m_theta = theta;
  }
  
  /**
   * Gets the position of the robot.
   * @return The position
   */
  /*@ pure @*/ public Vector<Float> getPosition()
  {
    return m_position;
  }
  
  /**
   * Gets the velocity of the robot.
   * @return The velocity
   */
  /*@ pure @*/ public Vector<Float> getVelocity()
  {
    return m_velocity;
  }
  
  /**
   * Gets the angle of orientation of the robot.
   * @return The angle (in radians)
   */
  /*@ pure @*/ public float getOrientation()
  {
    return m_theta;
  }
  
  @Override
  public String toString()
  {
    StringBuilder out = new StringBuilder();
    out.append("\u27e8");
    out.append("p:").append(m_position).append(",");
    out.append("v:").append(m_velocity).append(",");
    out.append("p\u03b8:").append(m_theta);
    out.append("\u27e9");
    return out.toString();
  }
  
  /**
   * BeepBeep function that extracts the x-position of a {@link RobotEvent}.
   */
  @SuppressWarnings("rawtypes")
  public static class GetPosition extends UnaryFunction<RobotEvent,Vector>
  {
    GetPosition()
    {
      super(RobotEvent.class, Vector.class);
    }

    @Override
    public Vector getValue(RobotEvent e)
    {
      return e.getPosition();
    }
    
    @Override
    public GetPosition duplicate(boolean with_state)
    {
      return this;
    }
    
    @Override
    public String toString()
    {
      return "p?";
    }
  }
  
  /**
   * BeepBeep function that extracts the velocity of a {@link RobotEvent}.
   */
  @SuppressWarnings("rawtypes")
  public static class GetVelocity extends UnaryFunction<RobotEvent,Vector>
  {
    GetVelocity()
    {
      super(RobotEvent.class, Vector.class);
    }

    @Override
    public Vector getValue(RobotEvent e)
    {
      return e.getVelocity();
    }
    
    @Override
    public GetVelocity duplicate(boolean with_state)
    {
      return this;
    }
    
    @Override
    public String toString()
    {
      return "v?";
    }
  } 
  
  /**
   * BeepBeep function that extracts the orientation of a {@link RobotEvent}.
   */
  public static class GetAngle extends UnaryFunction<RobotEvent,Float>
  {
    GetAngle()
    {
      super(RobotEvent.class, Float.class);
    }

    @Override
    public Float getValue(RobotEvent e)
    {
      return e.getOrientation();
    }
    
    @Override
    public GetAngle duplicate(boolean with_state)
    {
      return this;
    }
    
    @Override
    public String toString()
    {
      return "\u03b8?";
    }
  } 
}
