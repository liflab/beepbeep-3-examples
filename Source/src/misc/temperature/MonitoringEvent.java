/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2019 Sylvain Hallé

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
package misc.temperature;

/**
 * Parent class of all the input events in this example.
 */
public abstract class MonitoringEvent
{
  /**
   * The ID of the rack where the event is generated
   */
  private int m_rackId;
  
  /**
   * A timestamp indicating the moment when the event was created
   * (in seconds). This is the only modification we made to the events with
   * respect to the original example (which do not carry a timestamp).
   */
  private float m_timestamp;

  /**
   * Creates a new monitoring event
   * @param rack_id The ID of the rack where the event occurs
   */
  public MonitoringEvent(int rack_id)
  {
    super();
    m_rackId = rack_id;
    m_timestamp = (float) System.currentTimeMillis() / 1000f;
  }

  /**
   * Gets the event's rack ID
   * @return The rack ID
   */
  public int getRackID() 
  {
    return m_rackId;
  }
  
  /**
   * Gets the event's timestamp
   * @return The timestamp, in seconds
   */
  public float getTimestamp()
  {
    return m_timestamp;
  }
  
  /**
   * Sub-class of monitoring event containing a temperature reading.
   */
  public static class TemperatureEvent extends MonitoringEvent
  {
    /**
     * The temperature
     */
    private double m_temperature;
    
    /**
     * Creates a new temperature event
     * @param rack_id The ID of the rack where the event occurs
     * @param temperature The temperature
     */
    public TemperatureEvent(int rack_id, double temperature)
    {
      super(rack_id);
      m_temperature = temperature;
    }
    
    /**
     * Gets the event's temperature
     * @return The temperature
     */
    public double getTemperature()
    {
      return m_temperature;
    }
    
    @Override
    public String toString() 
    {
        return "TemperatureEvent(" + getTimestamp() + "," + getRackID() + ", " + m_temperature + ")";
    }
  }
  
  /**
   * Sub-class of monitoring event containing a voltage reading.
   */
  public static class PowerEvent extends MonitoringEvent
  {
    /**
     * The voltage
     */
    private double m_voltage;
    
    /**
     * Creates a new temperature event
     * @param rack_id The ID of the rack where the event occurs
     * @param voltage The voltage
     */
    public PowerEvent(int rack_id, double voltage)
    {
      super(rack_id);
      m_voltage = voltage;
    }
    
    /**
     * Gets the event's voltage
     * @return The voltage
     */
    public double getVoltage()
    {
      return m_voltage;
    }
    
    @Override
    public String toString() 
    {
        return "PowerEvent(" + getTimestamp() + "," + getRackID() + ", " + m_voltage + ")";
    }
  }
}