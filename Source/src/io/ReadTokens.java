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
package io;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.io.ReadStringStream;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.util.FindPattern;

/**
 * Read complete comma-separated tokens from the standard input.
 * You should first understand how {@link ReadStdin} works before look at this
 * example. In this example, we pipe the character strings of the
 * {@link ca.uqac.lif.cep.io.StreamReader StreamReader} into a 
 * {@link ca.uqac.lif.cep.input.CsvFeeder CsvFeeder} before sending its output
 * into the <tt>Print</tt> processor. 
 * <p>
 * Graphically, the processor chain is represented as follows:
 * <p>
 * <img
 *   src="{@docRoot}/doc-files/io/ReadTokens.png"
 *   alt="Processor graph">
 * <p>
 * The main difference of this chain is that the <tt>CsvFeeder</tt> only
 * outputs <em>complete</em> tokens, which are delimited by a comma. Suppose
 * you compile this program as a runnable JAR (for example,
 * <tt>read-tokens.jar</tt>) and run it in the same way as {@link ReadStdin},
 * using the named pipe <tt>mypipe</tt>.
 * <pre>
 * $ cat mypipe &gt; java -jar read-tokens.jar
 * </pre>
 * From a second command line window, you can now push strings to the program
 * by <tt>echo</tt>ing them to <tt>mypipe</tt>:
 * <pre>
 * $ echo "abc,def," &gt; mypipe
 * </pre>
 * The program will output
 * <pre>
 * abc
 * def
 * </pre>
 * Note here how each of <tt>abc</tt> and <tt>def</tt> have been printed on
 * <em>two</em> separate lines. This is because the CSV feeder broke the input
 * string into two events, since there are two commas that indicate the
 * presence of two tokens. This also means that the feeder waits until the
 * comma before outputting an event; hence writing:
 * <pre>
 * $ echo "gh" &gt; mypipe
 * </pre>
 * will result in no output. The CSV feeder buffers the character string until
 * it sees the token delimiter. Typing:
 * <pre>
 * $ echo "i,jkl," &gt; mypipe
 * </pre>
 * will produce
 * <pre>
 * ghi
 * jkl
 * </pre>
 * The usefulness of the CsvFeeder (or of other token feeders available in
 * BeepBeep) is to reconstruct complete tokens out of character strings.
 * Processors such as the {@link ca.uqac.lif.cep.io.StreamReader StreamReader}
 * read their input into chunks in such a way that a token can be split
 * across two successive chunks (its beginning in the first chunk, and its
 * end in the second).
 * 
 * @see ReadStdin
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class ReadTokens 
{

	public static void main(String[] args) throws ProcessorException, InterruptedException 
	{
		/* Read from stdin using a StringStreamReader */
		///
		ReadStringStream reader = new ReadStringStream(System.in);
		reader.setIsFile(false);
		
		/* We connect the reader to a pump, which will periodically ask
		 * the reader to read new characters from the input stream */
		Pump pump = new Pump(100);
		Thread pump_thread = new Thread(pump);
		Connector.connect(reader, pump);
		
		/* Create a CSV token feeder */
		FindPattern feeder = new FindPattern("(.*?),");
		Connector.connect(pump, feeder);
		
		/* Print the output of the feeder */
		Print print = new Print().setSeparator("\n");
		Connector.connect(feeder, print);
		
		/* Start the reader and enter an idle loop */
		pump_thread.start();
		while (true)
		{
			Thread.sleep(10000);
		}
		///

	}

}
