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
package io;

import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Read lines from a text file and convert them into numbers. This illustrates
 * the use of the {@link ca.uqac.lif.cep.io.ReadLines ReadLines} processor.
 * <p>
 * The expected output of the program is:
 * <pre>
 * 3,Integer
 * 1,Integer
 * 4,Integer
 * 1,Integer
 * 5,Integer
 * 9,Integer
 * 2.2,Float
 * Exception in thread "main" java.util.NoSuchElementException
 * 	at ca.uqac.lif.cep.UniformProcessor$UnaryPullable.pull(UniformProcessor.java:269)
 * 	at io.LineReaderExample.main(LineReaderExample.java:43)
 * </pre>
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class LineReaderExample 
{
	public static void main(String[] args) 
	{
		/// We obtain an InputStream from a file. Since we refer to a file
		// that lies within the source code's folders, we can access it using
		// the getResourceAsStream method. You could replace this with any
		// other instruction that returns an InputStream.
		InputStream is = LineReaderExample.class.getResourceAsStream("pi.txt");
		
		// We then instantiate a ReadLines processor, and instruct it to read
		// from the input stream we created
		ReadLines reader = new ReadLines(is);
		
		// The output of ReadLines are String events. We apply the function
		// NumberCast on these strings to convert them into number objects.
		ApplyFunction cast = new ApplyFunction(Numbers.numberCast);
		
		// The reader and cast processors are connected
		Connector.connect(reader, cast);
		
		// We then pull repeatedly on cast. Note the use of hasNext: since we
		// are reading from a file, eventually we reach the end of the file
		// and no more events can be produced.
		Pullable p = cast.getPullableOutput();
		while (p.hasNext())
		{
			Number n = (Number) p.next();
			System.out.println(n + "," + n.getClass().getSimpleName());
		}
		// Just for fun, let us try to pull one last time on p. This will
		// throw an exception
		p.pull();
		///
	}

}
