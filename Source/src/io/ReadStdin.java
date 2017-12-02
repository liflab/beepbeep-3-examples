/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

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

/**
 * Read bytes from the standard input ({@code stdin}). This program creates a
 * {@link ca.uqac.lif.cep.io.StreamReader StreamReader} that reads from the
 * standard input, and merely pushes whatever comes into to a
 * {@link ca.uqac.lif.cep.io.Print Print}
 * processor that reprints it to the standard output. The chain of processors
 * hence looks like this:  
 * <p>
 * <img
 *   src="{@docRoot}/doc-files/io/ReadStdin.png"
 *   alt="Processor graph">
 * <p>
 * In this picture, the leftmost processor is the {@code StreamReader}. As you
 * can see, it takes its input from the standard input; note how the input at
 * its left does not have the same shape as regular BeepBeep pipes. This is
 * to represent the fact that the processor does not receive events from a
 * BeepBeep processor, but rather reads a <em>system stream</em> from the
 * "outside world". In terms of BeepBeep processors, the {@code StreamReader}
 * has an input arity of zero.
 * <p>
 * A similar comment can be done for the {@code Print} processor. It receives
 * input events, but as far as BeepBeep is concerned, does not produce any
 * output events. Rather, it sends whatever it receives to the "outside world",
 * this time through the {@code stdout} system stream. This is also what does
 * the {@code Print} processor in the {@link PrintStdout} example; however,
 * the "stdout" output which was implicit in that example here is written
 * explicitly in the drawing.
 * <p>
 * As with {@link PrintStdout}, you can compile this program as a runnable JAR
 * file (e.g. {@code read-stdin.jar} and try it out on the command line.
 * Suppose you type:
 * <pre>
 * $ java -jar read-stdin.jar 
 * </pre>
 * Nothing happens; however, if you type a few characters and press
 * {@code Enter}, you should see the program reprint exactly what you typed
 * (followed by a comma, as the {@code Print} processor is instructed to¸
 * insert one between each event).
 * <p>
 * Let's try something slightly more interesting. If you are at a Unix-like
 * command prompt, you can create a
 * <a href="https://en.wikipedia.org/wiki/Named_pipe">named pipe</a>. Let us
 * create one with the name {@code mypipe}:
 * <pre>
 * $ mkfifo mypipe
 * </pre>
 * Now, let us launch {@code read-stdin.jar}, by redirecting {@code mypipe}
 * into its standard input:
 * <pre>
 * $ cat mypipe &gt; java -jar read-stdin.jar
 * </pre>
 * If you open another command prompt, you can then push characters into
 * {@code mypipe}; for example using the command {@code echo}. Hence, if you
 * type
 * <pre>
 * $ echo "foo" &gt; mypipe
 * </pre>
 * you should see the string {@code foo} being immediately printed at the
 * other command prompt. This happens because {@code read-stdin.jar}
 * continuously polls its standard input for new characters, and pushes them
 * down the processor chain whenever it receives some.
 * <p>
 * As you can see, the use of stream readers in BeepBeep, combined with
 * system pipes on the command line, makes it possible for BeepBeep to
 * interact with other programs from the command line, in exactly the same way
 * Unix programs can be connected into each other.
 * <p>
 * This can be used to read a file. Instead of redirecting a named pipe to
 * the program, one can use the {@code cat} command with an actualy filename:
 * <pre>
 * $ cat somefile.txt &gt; java -jar read-stdin.jar
 * </pre>
 * This will have for effect of reading and pushing the entire contents of
 * {@code somefile.txt} into the processor chain.
 * However, one can also read a file directly from BeepBeep; see the
 * {@link ReadFile} example.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class ReadStdin
{
	public static void main(String[] args) throws ProcessorException, InterruptedException 
	{
		/* We create a stream reader, and instruct it to read bytes from
		 * the standard input (represented in Java by the System.in object).
		 * We must tell the reader that it is not reading from a file, and
		 * that it should push whatever it receives. */
		ReadStringStream reader = new ReadStringStream(System.in);
		reader.setIsFile(false);
		
		/* We connect the reader to a pump, which will periodically ask
		 * the reader to read new characters from the input stream */
		Pump pump = new Pump(100);
		Thread pump_thread = new Thread(pump);
		Connector.connect(reader, pump);
		
		/* We connect the output of the stream reader to a Print processor,
		 * that will merely re-print to the standard output what was received
		 * from the standard input. */
		Print print = new Print();
		Connector.connect(pump, print);
		
		/* We need to call start() on the pump's thread so that it can start
		 * listening to its input stream. */
		pump_thread.start();
		
		/* Since our program does nothing else, it would stop right away.
		 * We put here an idle loop. You can stop it by pressing Ctrl+C. */
		while (true)
		{
			Thread.sleep(10000);
		}
	}

}
