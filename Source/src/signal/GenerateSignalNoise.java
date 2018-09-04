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
package signal;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.VariableStutter;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Randomize;

public class GenerateSignalNoise extends GroupProcessor
{
  public GenerateSignalNoise(float noise, Object[] l_values, Object[] l_numbers)
  {
    super(0, 2);
    QueueSource values = new QueueSource();
    values.setEvents(l_values);
    values.loop(false);
    QueueSource numbers = new QueueSource();
    numbers.setEvents(l_numbers);
    numbers.loop(false);
    VariableStutter vs = new VariableStutter();
    Connector.connect(values, 0, vs, 0);
    Connector.connect(numbers, 0, vs, 1);
    Fork fork1 = new Fork(2);
    Connector.connect(vs, fork1);
    TurnInto one = new TurnInto(1);
    Connector.connect(fork1, 0, one, 0);
    Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(one, sum_one);
    Cumulate sum_values = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
    Connector.connect(fork1, 1, sum_values, 0);
    Fork fork2 = new Fork(2);
    Connector.connect(sum_values, fork2);
    Randomize rand = new Randomize(-0.5f * noise, 0.5f * noise);
    Connector.connect(fork2, 0, rand, 0);
    ApplyFunction add = new ApplyFunction(Numbers.addition);
    Connector.connect(rand, 0, add, 0);
    Connector.connect(fork2, 1, add, 1);
    addProcessors(values, numbers, vs, fork1, one, sum_one, fork2, rand, add, sum_values);
    associateOutput(0, sum_one, 0);
    associateOutput(1, add, 0);
  }  
}
