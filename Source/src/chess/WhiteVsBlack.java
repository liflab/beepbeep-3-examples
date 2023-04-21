/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2023 Sylvain Hallé

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
package chess;

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.connect;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.io.Print.Println;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.KeepLast;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.Strings;

/**
 * Process a database of chess games to calculate the number of white wins
 * vs.<!-- --> black wins. This example is a follow-up to a
 * <a href="https://web.archive.org/web/20140119221101/https://tomhayden3.com/2013/12/27/chess-mr-job/">2013
 * post by Tom Hayden</a>, who performed the same calculation as a MapReduce
 * job for <a href="https://hadoop.apache.org/">Hadoop</a>. The original input
 * file, called the "<a href="https://web.archive.org/web/20140119221101/http://www.top-5000.nl/pgn.htm">millionbase
 * archive</a>", is 1.74 GB big and contains about 2 million chess games.
 * <p>
 * The surprising finding about this example is that, while the Hadoop job
 * reportedly takes <strong>26 minutes</strong> to terminate, the BeepBeep
 * pipeline  takes a mere <strong>35 seconds</strong> to compute the same
 * result.
 * <p>
 * The program calculates the number of occurrences of each possible outcome:
 * <ol>
 * <li>1-0 (White wins)</li>
 * <li>&half;-&half; (draw)</li>
 * <li>0-1 (Black wins)</li>
 * <li>unknown (marked as a "*" in the database)</li>
 * </ol>
 * It does so with the following processor chain:
 * <p>
 * <img src="{@docRoot}/doc-files/chess/WhiteVsBlack.png" alt="Pipeline"/>
 * <p>
 * Running the program should produce the output:
 * <pre>
 * {[Result "*"]=221.0, [Result "1-0"]=852305.0, [Result "1/2-1/2"]=690934.0, [Result "0-1"]=653728.0}
 * Total time: 37629 ms
 * </pre>
 * <strong>Note:</strong> the example deliberately makes use of multiple
 * syntactical shortcuts to reduce the size of the code as much as possible
 * (at the price of some legibility).
 * 
 * @author Sylvain Hallé
 */
public class WhiteVsBlack
{

	public static void main(String[] args) throws IOException
	{
		/* The path to the "millionbase" archive. The examples repository does
		 * *not* contain the file due to its size, but it can be downloaded from
		 * the URL provided in the Javadoc above. Alternatively, any file in the
		 * PGN format can be used in its place. */
		String filename = "millionbase-2.22.pgn";
		
		/* Pump lines from the local PGN file. */
		Pump pump = (Pump) connect(new ReadLines(
				new FileInputStream(new File(filename))), new Pump());
		
		/* Retain only lines that start with the string "[Result" */ 
		Processor fork = connect(pump, new Fork());
		Processor filter = connect(fork, TOP, new Filter(), TOP);
		Processor result = connect(fork, BOTTOM, 
				new ApplyFunction(new FunctionTree(Strings.startsWith, StreamVariable.X, new Constant("[Result"))), INPUT);
		connect(result, OUTPUT, filter, BOTTOM);
		
		/* Pipe the output to a slice that increments a counter for each outcome. */
		Processor slice = connect(filter,	new Slice(
				new IdentityFunction(1), new GroupProcessor(1, 1) {
					{
						TurnInto one = new TurnInto(1);
						Processor sum = connect(one, 
								new Cumulate(new CumulativeFunction<Number>(Numbers.addition)));
						addProcessors(one, sum).associateInput(one).associateOutput(sum);
					}
				}));
		
		/* Keep only the last event of the pipeline and print it. */
		connect(slice, new KeepLast(), new Println());
		
		/* Start the pump to process the file, and count the time it takes. */
		long start = System.currentTimeMillis();
		pump.run();
		long stop = System.currentTimeMillis();
		System.out.println("Total time: " + (stop - start) + " ms");
	}

}
