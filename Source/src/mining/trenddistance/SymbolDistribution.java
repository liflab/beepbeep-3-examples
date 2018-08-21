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

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;

import java.util.HashMap;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.io.ReadStringStream;
import ca.uqac.lif.cep.peg.MapDistance;
import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.FindPattern;

/**
 * Trend distance based on the statistical distribution of symbols in a
 * stream.
 * <p>
 * The parameters of the <tt>TrendDistance</tt> processor in this example
 * are as follows:
 * <table>
 * <tr><th>Parameter</th><th>Value</th></tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/WidthParameter.png" alt="Window Width" title="The width of the window"></td>
 *   <td>9</td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/BetaProcessor.png" alt="Beta processor" title="The processor that computes the pattern over the current input stream"></td>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/SymbolDistribution-PatternProcessor.png" alt="Processor chain"></td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/PatternParameter.png" alt="Reference Pattern" title="The reference pattern"></td>
 *   <td>A map that associates each symbol with a number of occurrences. The map is:<table><tr><th>a</th><td>6</td></tr><tr><th>b</th><td>1</td></tr><tr><th>c</th><td>2</td></tr></table></td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/DistanceFunction.png" alt="Distance Function" title="The function that computes the distance with respect to the reference pattern"></td>
 *   <td><img src="{@docRoot}/doc-files/mining/MapDistance.png" alt="Distance Function"> ({@link ca.uqac.lif.cep.peg.MapDistance MapDistance})</td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/ComparisonFunction.png" alt="Comparison Function" title="The function that compares that distance with a given threshold"></td>
 *   <td><img src="{@docRoot}/doc-files/mining/LessThanOrEqual.png" alt="&leq;"></td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/DistanceThreshold.png" alt="Distance Threshold" title="The distance threshold"></td>
 *   <td>2</td>
 * </tr>

 * </table>
 * 
 * @author Sylvain Hallé
 *
 */
public class SymbolDistribution 
{
	@SuppressWarnings("rawtypes")
	public static void main(String[] args)
	{
		ReadStringStream reader = new ReadStringStream(SymbolDistribution.class.getResourceAsStream("SymbolDistribution.txt"));
		FindPattern feeder = new FindPattern("(.*?),");
		Connector.connect(reader, feeder);
		GroupProcessor counter = new GroupProcessor(1, 1);
		{
			TurnInto one = new TurnInto(new Constant(1));
			counter.associateInput(INPUT, one, INPUT);
			Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
			Connector.connect(one, sum_one);
			counter.associateOutput(OUTPUT, sum_one, OUTPUT);
			counter.addProcessors(one, sum_one);
		}
		Slice slicer = new Slice(new IdentityFunction(1), counter);
		HashMap<Object,Object> pattern = MapDistance.createMap("a", 6, "b", 1, "c", 2);
		TrendDistance<HashMap,Number,Number> alarm = new TrendDistance<HashMap,Number,Number>(pattern, 9, slicer, new FunctionTree(Numbers.absoluteValue, 
				new FunctionTree(MapDistance.instance, StreamVariable.X, StreamVariable.Y)), 2, Numbers.isLessThan);
		Connector.connect(feeder, alarm);
		Pullable p = alarm.getPullableOutput();
		boolean b = true;
		for (int i = 0; b && i < 10; i++)
		{
			b = (Boolean) p.pull();
			System.out.println(b);
		}
	}
}
