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
package episodes;

import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Abstract class representing events in a pressure stream
 */
public abstract class PressureEvent
{
  /**
   * Static reference to a "new day" event
   */
  public static final NewDay NEW_DAY = new NewDay();
  
  /**
   * Static reference to an "episode start" event
   */
  public static final EpisodeStart EPISODE_START = new EpisodeStart();
  
  /**
   * Static reference to an "episode end" event
   */
  public static final EpisodeEnd EPISODE_END = new EpisodeEnd();
  
  /**
   * Event representing the start of a new day
   */
  public static class NewDay extends PressureEvent
  {
    @Override
    public String toString()
    {
      return "\u2737";
    }
  }

  /**
   * Event representing the start of an episode
   */
  public static class EpisodeStart extends PressureEvent
  {
    @Override
    public String toString()
    {
      return "\u2191";
    }
  }
  
  /**
   * Event representing the end of an episode
   */
  public static class EpisodeEnd extends PressureEvent
  {
    @Override
    public String toString()
    {
      return "\u2193";
    }
  }
  
  /**
   * Event containing a pressure reading
   */
  public static class Reading extends PressureEvent
  {
    /**
     * The value in this event
     */
    protected final int m_value;
    
    /**
     * Creates a new reading event
     * @param value The value in this event
     */
    public Reading(int value)
    {
      super();
      m_value = value;
    }
    
    @Override
    public String toString()
    {
      return Integer.toString(m_value);
    }
    
    /**
     * Gets the value in this event
     * @return The value
     */
    public int getValue()
    {
      return m_value;
    }
  }
  
  /**
   * BeepBeep function checking if an event is an instance of
   * "new day"
   */
  public static class IsNewDay extends UnaryFunction<PressureEvent,Boolean>
  {
    /**
     * A public reference to a single instance of the function
     */
    public static transient final IsNewDay instance = new IsNewDay();
    
    protected IsNewDay()
    {
      super(PressureEvent.class, Boolean.class);
    }
    
    @Override
    public Boolean getValue(PressureEvent x)
    {
      return x instanceof NewDay;
    }
  }
  
  /**
   * BeepBeep function checking if an event is an instance of
   * reading
   */
  public static class IsReading extends UnaryFunction<PressureEvent,Boolean>
  {
    /**
     * A public reference to a single instance of the function
     */
    public static transient final IsReading instance = new IsReading();
    
    protected IsReading()
    {
      super(PressureEvent.class, Boolean.class);
    }
    
    @Override
    public Boolean getValue(PressureEvent x)
    {
      return x instanceof Reading;
    }
  }
  
  /**
   * BeepBeep function checking if an event is an instance of
   * "episode start"
   */
  public static class IsEpisodeStart extends UnaryFunction<PressureEvent,Boolean>
  {
    /**
     * A public reference to a single instance of the function
     */
    public static transient final IsEpisodeStart instance = new IsEpisodeStart();
    
    protected IsEpisodeStart()
    {
      super(PressureEvent.class, Boolean.class);
    }
    
    @Override
    public Boolean getValue(PressureEvent x)
    {
      return x instanceof EpisodeStart;
    }
  }
  
  /**
   * BeepBeep function checking if an event is an instance of
   * "episode end"
   */
  public static class IsEpisodeEnd extends UnaryFunction<PressureEvent,Boolean>
  {
    /**
     * A public reference to a single instance of the function
     */
    public static transient final IsEpisodeEnd instance = new IsEpisodeEnd();
    
    protected IsEpisodeEnd()
    {
      super(PressureEvent.class, Boolean.class);
    }
    
    @Override
    public Boolean getValue(PressureEvent x)
    {
      return x instanceof EpisodeEnd;
    }
  }
  
  /**
   * BeepBeep function checking if an event is an instance of
   * "episode end"
   */
  public static class GetValue extends UnaryFunction<Reading,Number>
  {
    /**
     * A public reference to a single instance of the function
     */
    public static transient final GetValue instance = new GetValue();
    
    protected GetValue()
    {
      super(Reading.class, Number.class);
    }
    
    @Override
    public Number getValue(Reading x)
    {
      return x.getValue();
    }
  }
  
}
