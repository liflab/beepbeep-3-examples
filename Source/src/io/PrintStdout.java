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

import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.cli.Print;

/**
 * Print events to the standard input. In this simple example,
 * we push numbers 0 to 4 at one-second intervals. The expected output
 * of this program should be:
 * <pre>
 * 0,1,2,3,4,
 * </pre>
 * The processor chain of this example is very simple, and consists of a
 * single processor:
 * <p>
 * <img
 *   src="{@docRoot}/doc-files/io/Print.png"
 *   alt="Processor graph">
 * <p>
 * Let us use this program from the command-line. Suppose you compile
 * this program into a runnable JAR file called {@code printstdout.jar}.
 * You can then run this program like any other Java JAR as follows:
 * <pre>
 * $ java -jar printstdout.jar
 * </pre> 
 * However, since this program writes to the standard output, you can
 * redirect it to a file like any other program too. For
 * example, writing this to the command line will redirect ("pipe") the output
 * to a file called {@code my-file.txt}:
 * <pre>
 * $ java -jar printstdout.jar &gt; my-file.txt
 * </pre>
 * Since the output events are produced at the rate of one per second, it will
 * take five seconds for the file to be written. If you try to open it before
 * (for example, in a text editor), you will only see the events that have
 * been pushed to it so far. 
 * <p>
 * The output can be redirected to other programs as well; for example, to
 * redirect the output to the <a href="https://en.wikipedia.org/wiki/Wc_(Unix)">wc</a>
 * command to count the number of characters in the output:
 * <pre>
 * $ java -jar printstdout.jar | wc -m
 * </pre>
 * Here, the standard output of the Java program is redirected into the
 * standard input of the {@code wc} command.
 * 
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class PrintStdout
{
	public static void main(String[] args) throws InterruptedException
	{
		/* We create a print processor and disable the use of colors in its
		 * output. By default, print sends its events to the computer's
		 * standard output (stdout) stream. */
		Print print = new Print().setAnsi(false);
		
		/* We get a hold of the Pushable object to push events to the print
		 * processor. In this very simple example, we push numbers 0 to 4
		 * at 1-second intervals. */
		Pushable p = print.getPushableInput();
		for (int i = 0; i < 5; i++)
		{
			p.push(i);
			Thread.sleep(1000); // Wait one second
		}
	}
}
