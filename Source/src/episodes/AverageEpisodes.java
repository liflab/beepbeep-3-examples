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
import ca.uqac.lif.cep.functions.TurnInto;

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
import ca.uqac.lif.cep.util.Sets;

/**
 * Computes the list of average values of each episode, grouped by day.
 * For example, given this stream of pressure events:
 * <blockquote>
 * ✸ ↑ 151 142 ↓ ↑ 148 149 144 ↓ ✸ ↑ 150 142 ↓ ✸
 * </blockquote>
 * the processor should output {146.5, 147} (averages of 1st and 2nd
 * episode of first day) followed by {146}
 * (average of single episode of second day).
 * Graphically, this can be represented by the following processor chain:
 * <p>
 * <img src="{@docRoot}/doc-files/episodes/AvgEpisodes.png" alt="Processor chain">
 * 
 * @author Sylvain Hallé
 */
public class AverageEpisodes
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
        new PressureEvent.Reading(148),
        new PressureEvent.Reading(149),
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
    GroupProcessor avg = new GroupProcessor(1, 1);
    {
      Fork f1 = new Fork(2);
      ApplyFunction is_reading = new ApplyFunction(PressureEvent.IsReading.instance);
      Filter filter = new Filter();
      connect(f1, TOP, filter, LEFT);
      connect(f1, BOTTOM, is_reading, INPUT);
      connect(is_reading, OUTPUT, filter, BOTTOM);
      ApplyFunction get_reading = new ApplyFunction(PressureEvent.GetValue.instance);
      connect(filter, get_reading);
      Fork f2 = new Fork(2);
      connect(get_reading, f2);
      Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
      connect(f2, TOP, sum, INPUT);
      TurnInto one = new TurnInto(1);
      connect(f2, BOTTOM, one, INPUT);
      Cumulate count = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
      connect(one, count);
      ApplyFunction div = new ApplyFunction(Numbers.division);
      connect(sum, OUTPUT, div, TOP);
      connect(count, OUTPUT, div, BOTTOM);
      avg.associateInput(INPUT, f1, INPUT);
      avg.associateOutput(OUTPUT, div, OUTPUT);
      avg.addProcessors(f1, is_reading, filter, get_reading, f2, sum, one, count, div);
    }
    
    // Group by day
    GroupProcessor by_day = new GroupProcessor(1, 1);
    {
      Fork f = new Fork(2);
      ApplyFunction is_end = new ApplyFunction(PressureEvent.IsEpisodeEnd.instance);
      ResetLast rst_by_day = new ResetLast(avg);
      connect(f, TOP, rst_by_day, LEFT);
      connect(f, BOTTOM, is_end, INPUT);
      connect(is_end, OUTPUT, rst_by_day, BOTTOM);
      Sets.PutIntoNew put = new Sets.PutIntoNew();
      connect(rst_by_day, put);
      by_day.associateInput(INPUT, f, INPUT);
      by_day.associateOutput(OUTPUT, put, OUTPUT);
      by_day.addProcessors(f, is_end, rst_by_day, put);
    }
    
    // Reset the "max reading" at every new day event
    Fork f = new Fork(2);
    connect(episode_stream, f);
    ApplyFunction is_new_day = new ApplyFunction(PressureEvent.IsNewDay.instance);
    connect(f, BOTTOM, is_new_day, INPUT);
    ResetLast rst_by_day = new ResetLast(by_day);
    connect(f, TOP, rst_by_day, LEFT);
    connect(is_new_day, OUTPUT, rst_by_day, BOTTOM);
        
    // Run the chain on the input source
    Pullable p = rst_by_day.getPullableOutput();
    while (p.hasNext())
    {
      System.out.println(p.next());
    }
  }

}
