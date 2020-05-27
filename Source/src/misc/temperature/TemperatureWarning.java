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
 * A temperature warning message
 */
public class TemperatureWarning
{
  /**
   * The ID of the rack where the event is generated
   */
  private int m_rackId;
  
  /**
   * The average temperature
   */
  protected double m_averageTemp;
  
  /**
   * Creates a new temperature alert event
   * @param rack_id The ID of the rack where the event occurs
   */
  public TemperatureWarning(int rack_id, double average_temp)
  {
    super();
    m_rackId = rack_id;
    m_averageTemp = average_temp;
  }
  
  /**
   * Gets the event's rack ID
   * @return The rack ID
   */
  public int getRackID() 
  {
    return m_rackId;
  }
  
  @Override
  public String toString() 
  {
      return "TemperatureAlert(" + getRackID() + ")";
  }
}
