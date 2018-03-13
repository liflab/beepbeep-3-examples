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
package plots;

import ca.uqac.lif.cep.Connector;
import static ca.uqac.lif.cep.Connector.TOP;
import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.mtnp.UpdateTable;
import ca.uqac.lif.cep.mtnp.UpdateTableStream;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tmf.QueueSource;

/**
 * Update a table from two streams of numbers.
 * This example generates dummy (x,y) pairs of numbers, accumulates them
 * into a table, and prints the table made from these numbers.
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class UpdateTableStreamExample
{

	public static void main(String[] args) throws InterruptedException
	{
		/// We create two streams of numbers
		QueueSource src1 = new QueueSource().setEvents(1, 2, 3, 4, 5);
		QueueSource src2 = new QueueSource().setEvents(2, 3, 5, 7, 4);

		/* These x-stream and y-stream are then pushed into an
		 * {@link UpdateTableStream} processor. We instantiate this processor,
		 * telling it to create an empty {@link Table} object with two
		 * columns, called "x" and "y". */
		UpdateTable table = new UpdateTableStream("x", "y");
		
		/* This creates a processor with two input streams, one for the "x"
		 * values, and the other for the "y" values. Each pair of values from
		 * the x and y streams is used to append a new line to the (initially
		 * empty) table. We connect the two
		 * outputs of the random processor to these two inputs. */
		Connector.connect(src1, OUTPUT, table, TOP);
		Connector.connect(src2, OUTPUT, table, BOTTOM);
		
		/* We print the contents of the resulting table. */
		Pump pump = new Pump();
		Print print = new Print().setSeparator("\n");
		Connector.connect(table, pump, print);

		/* Ready to start the pump! */
		pump.turn(4);
		///
	}
}
