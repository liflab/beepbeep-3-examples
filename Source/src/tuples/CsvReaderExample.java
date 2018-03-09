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
package tuples;

import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.tuples.Tuple;
import ca.uqac.lif.cep.tuples.TupleFeeder;

/**
 * Read tuples from a CSV file using the
 * {@link ca.uqac.lif.cep.tuples.TupleFeeder TupleFeeder} processor.
 * Graphically, this chain of processor can be described as follows:
 * <p>
 * <img src="{@docRoot}/doc-files/tuples/CsvReaderExample.png" alt="Processor graph">
 * <p>
 * The output of this program is:
 * <pre>
 * ((A,3),(B,2),(C,1))
 * ((A,1),(B,7),(C,1))
 * ((A,4),(B,1),(C,2))
 * ((A,1),(B,8),(C,3))
 * ((A,6),(B,3),(C,5))
 * </pre>
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class CsvReaderExample
{
	public static void main(String[] args) 
	{
		///
		InputStream is = CsvReaderExample.class.getResourceAsStream("file1.csv");
		ReadLines reader = new ReadLines(is);
		TupleFeeder tuples = new TupleFeeder();
		Connector.connect(reader, tuples);
		Pullable p = tuples.getPullableOutput();
		Tuple tup = null;
		while (p.hasNext())
		{
			tup = (Tuple) p.next();
			System.out.println(tup);
		}
		///
		//* Let us now examine how to interact with a Tuple object
		Object o = tup.get("A");
		System.out.println(o + "," + o.getClass().getSimpleName());
		//*
	}
}
