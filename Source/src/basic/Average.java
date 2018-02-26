/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.LEFT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.RIGHT;
import util.UtilityMethods;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Compute the cumulative average of a list of numbers. The cumulative average
 * is the average of all the numbers processed so far.
 * <p>
 * Consider for example the stream of numbers 2, 7, 1, 8, &hellip;. After
 * reading the first event, the cumulative average is 2&nbsp;&div;&nbsp;1 = 2.
 * After reading the second event, the average is (2 + 7)&nbsp;&div;&nbsp;2,
 * and after reading the third, the average is (2 + 7 + 1)&nbsp;&div;&nbsp;3
 * = 3.33 --and so on.
 * <p>
 * This example illustrates the use of the {@link Cumulate} and
 * {@link CumulativeFunction} objects.
 * <p>
 * We will compute this average by computing the cumulative sum of a stream
 * of numbers, and dividing it by the value of a counter that increments by
 * 1 each time it is pulled.
 * Represented graphically, this example corresponds to the following chain
 * of processors:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/Average.png" alt="Processor graph">
 * <p>
 * The output of this program should look like this:
 * <pre>
 * The cumulative average is...
 * 2.0, 4.5, 3.3333333, 4.5, 4.0, 4.6666665, 4.142857, 4.625, 4.3333335, 
* </pre>
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class Average
{

	public static void main(String[] args)
	{
		/* Hello! */
		UtilityMethods.printGreeting();
		
		/* Let us first create a source of arbitrary numbers. This is done
		 * by instantiating a {@link QueueSource} object, and giving to it
		 * an array of numbers. */
		///
		QueueSource numbers = new QueueSource(1);
		numbers.setEvents(new Object[]{2, 7, 1, 8, 2, 8, 1, 8, 2, 8,
				4, 5, 9, 0, 4, 5, 2, 3, 5, 3, 6, 0, 2, 8, 7});
		
		/* We pipe the output of this processor to a cumulative processor.
		 * Such a processor applies a function with two arguments: the first
		 * is the event that is being received, and the second is the output
		 * value of the cumulative function at the previous step. In our
		 * case, the function we use is addition over numbers; hence the
		 * output is the cumulative sum of all numbers received so far. */
		Cumulate sum_proc = new Cumulate(
				new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(numbers, OUTPUT, sum_proc, INPUT);

		/* Now we create a source of numbers acting as a counter. */
		QueueSource counter = new QueueSource().setEvents(1, 2, 3, 4, 5, 6, 7);


		/* Divide one trace by the other; the output is the cumulative average
		 * of all numbers seen so far. */
		ApplyFunction division = new ApplyFunction(Numbers.division);
		Connector.connect(sum_proc, OUTPUT, division, LEFT);
		Connector.connect(counter, OUTPUT, division, RIGHT);
		///
		
		/* Extract the first 20 events from that pipe and print them. */
		Pullable p = division.getPullableOutput();
		System.out.println("The cumulative average is...");
		for (int i = 0; i < 20; i++)
		{
			float value = (Float) p.pull();
			System.out.print(value + ", ");
			UtilityMethods.pause(1000);
		}
		System.out.println("done!");
	}
}