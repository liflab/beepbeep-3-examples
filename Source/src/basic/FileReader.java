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

import java.io.InputStream;

import util.UtilityMethods;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.io.ReadLines;

/**
 * Read an input stream from a text file line by line and show the output.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class FileReader
{
	public static void main(String[] args)
	{
		/* Get an input stream on some resource. Here we read a file
		 * that resides with the source code by using the getResourceAsStream()
		 * method. (This is standard Java and not specific to BeepBeep.) */
		InputStream stream = FileReader.class.getResourceAsStream("numbers.txt");
		
		/* Give this stream to a LineReader processor. The LineReader
		 * reads the lines of a file one by one, and outputs each line
		 * as an event (a String object). */
		ReadLines reader = new ReadLines(stream);
		
		/* The addCrlf() method tells the line reader *not* to append a
		 * carriage return at the end of each event. There exist situations where
		 * this might be useful, but not here. */
		reader.addCrlf(false);
		
		/* Get a reference to the output pullable of the LineReader. */
		Pullable p = reader.getPullableOutput();
		
		/* We exploit the fact that p can be used like an iterator to
		 * write the loop as follows: */
		for (Object o : p)
		{
			/* Note that the events produced by the LineReader are *strings*;
			 * here, they just happen to be strings that contain numbers. */
			System.out.printf("The event is %s\n", o);
			// Sleep a little so you have time to read!
			UtilityMethods.pause(1000);
		}
	}
}
