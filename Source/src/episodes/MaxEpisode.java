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

import static ca.uqac.lif.cep.Connector.connect;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.LEFT;
import static ca.uqac.lif.cep.Connector.TOP;
import static ca.uqac.lif.cep.Connector.OUTPUT;

import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.ResetLast;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Computes the maximum value of all episodes in each day.
 * For example, given this stream of pressure events:
 * <blockquote>
 * ✸ ↑ 151 142 ↓ ↑ 149 148 144 ↓ ✸ ↑ 150 142 ↓ ✸
 * </blockquote>
 * the processor should output 151 (maximum value of first day) followed
 * by 150 (maximum value of second day).
 * Graphically, this can be represented by the following processor chain:
 * <p>
 * <img src="{@docRoot}/doc-files/episodes/MaxEpisodes.png" alt="Processor chain">
 * 
 * @author Sylvain Hallé
 */
public class MaxEpisode
{
  public static void main(String[] args)
  {
    // Create a simple source of pressure events to illustrate the program
    QueueSource episode_stream = new QueueSource();
    episode_stream.setEvents(
        PressureEvent.NEW_DAY,
        PressureEvent.EPISODE_START,
        new PressureEvent.Reading(151),
        new PressureEvent.Reading(142),
        PressureEvent.EPISODE_END,
        PressureEvent.EPISODE_START,
        new PressureEvent.Reading(149),
        new PressureEvent.Reading(148),
        new PressureEvent.Reading(144),
        PressureEvent.EPISODE_END,
        PressureEvent.NEW_DAY,
        PressureEvent.EPISODE_START,
        new PressureEvent.Reading(150),
        new PressureEvent.Reading(142),
        PressureEvent.EPISODE_END,
        PressureEvent.NEW_DAY).loop(false);
    
    // Create a group that computes the maximum value of all
    // readings received
    GroupProcessor max_of_days = new GroupProcessor(1, 1);
    {
      Fork f = new Fork(2);
      ApplyFunction is_reading = new ApplyFunction(PressureEvent.IsReading.instance);
      Filter filter = new Filter();
      connect(f, TOP, filter, LEFT);
      connect(f, BOTTOM, is_reading, INPUT);
      connect(is_reading, OUTPUT, filter, BOTTOM);
      ApplyFunction get_reading = new ApplyFunction(PressureEvent.GetValue.instance);
      connect(filter, get_reading);
      Cumulate max = new Cumulate(new CumulativeFunction<Number>(Numbers.maximum));
      connect(get_reading, max);
      max_of_days.associateInput(INPUT, f, INPUT);
      max_of_days.associateOutput(OUTPUT, max, OUTPUT);
      max_of_days.addProcessors(f, is_reading, filter, get_reading, max);
    }
    
    // Reset the "max reading" at every new day event
    Fork f = new Fork(2);
    connect(episode_stream, f);
    ApplyFunction is_new_day = new ApplyFunction(PressureEvent.IsNewDay.instance);
    connect(f, BOTTOM, is_new_day, INPUT);
    ResetLast reset = new ResetLast(max_of_days);
    connect(f, TOP, reset, LEFT);
    connect(is_new_day, OUTPUT, reset, BOTTOM);
    
    // Run the chain on the input source
    Pullable p = reset.getPullableOutput();
    while (p.hasNext())
    {
      System.out.println(p.next());
    }
  }

}
