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
package mining.trenddistance;

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.TOP;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ArgumentPlaceholder;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.tmf.ConstantProcessor;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Trend distance based on the average of values in a
 * stream. In this example, we consider a stream of numerical values.
 * The reference trend <tt>P</tt> is a single number, representing an
 * average. The <tt>TrendProcessor</tt> emits an alarm when the
 * <em>difference</em> between <tt>P</tt> and the average of values over a
 * sliding window of width 3 is greater than ½. Note that here, the distance
 * is measured as the absolute difference between the reference average
 * and the observed average. One can also compute the distance in
 * terms of percentages; see {@link AverageValueRelative}.
 * <p>
 * The parameters of the <tt>TrendDistance</tt> processor in this example
 * are as follows:
 * <table>
 * <tr><th>Parameter</th><th>Value</th></tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/WidthParameter.png" alt="Window Width" title="The width of the window"></td>
 *   <td>3</td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/BetaProcessor.png" alt="Beta processor" title="The processor that computes the pattern over the current input stream"></td>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/AverageProcessor.png" alt="Processor chain"></td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/PatternParameter.png" alt="Reference Pattern" title="The reference pattern"></td>
 *   <td>6</td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/DistanceFunction.png" alt="Distance Function" title="The function that computes the distance with respect to the reference pattern"></td>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/AbsoluteDifference.png" alt="Distance Function"></td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/ComparisonFunction.png" alt="Comparison Function" title="The function that compares that distance with a given threshold"></td>
 *   <td><img src="{@docRoot}/doc-files/mining/LessThanOrEqual.png" alt="&leq;"></td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/DistanceThreshold.png" alt="Distance Threshold" title="The distance threshold"></td>
 *   <td>½</td>
 * </tr>
 * </table>
 * Consider for example the input stream that starts with:
 * <pre>
 * 6.1, 5.9, 6, 6.7, 6.7, 6.7, &hellip;
 * </pre>
 * On the first window of width 3 (6.1, 5.9, 6), the average of the values is
 * 6, and |6-6| &leq; ½. This average lies between 6-½ and 6+½ for windows 2
 * and 3 as well. On the 4th window of width 3 (6.7, 6.7, 6.7), the
 * average is 6.7, and |6-6.7| &gt; ½; this time, the <tt>TrendDistance</tt>
 * processor returns false.
 * 
 * @author Sylvain Hallé
 *
 */
public class AverageValueAbsolute 
{
	public static void main(String[] args)
	{
		GroupProcessor average = new GroupProcessor(1, 1);
		{
			Fork fork = new Fork(2);
			average.associateInput(INPUT, fork, INPUT);
			CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Numbers.addition));
			Connector.connect(fork, TOP, sum, INPUT);
			ConstantProcessor one = new ConstantProcessor(new Constant(1));
			Connector.connect(fork, BOTTOM, one, INPUT);
			CumulativeProcessor sum_one = new CumulativeProcessor(new CumulativeFunction<Number>(Numbers.addition));
			Connector.connect(one, sum_one);
			FunctionProcessor div = new FunctionProcessor(Numbers.division);
			Connector.connect(sum, OUTPUT, div, TOP);
			Connector.connect(sum_one, OUTPUT, div, BOTTOM);
			average.associateOutput(OUTPUT, div, OUTPUT);
			average.addProcessors(fork, sum, one, sum_one, div);
		}
		TrendDistance<Number,Number,Number> alarm = new TrendDistance<Number,Number,Number>(6, 3, average, new FunctionTree(Numbers.absoluteValue, 
				new FunctionTree(Numbers.subtraction, new ArgumentPlaceholder(0), new ArgumentPlaceholder(1))), 0.5, Numbers.isLessThan);
		QueueSource source = new QueueSource();
		source.setEvents(6.1, 5.9, 6, 6.7, 6.7, 6.7);
		Connector.connect(source, alarm);
		Pullable p = alarm.getPullableOutput();
		boolean b = true;
		for (int i = 0; b && i < 10; i++)
		{
			b = (Boolean) p.pull();
			System.out.println(b);
		}
	}
}
