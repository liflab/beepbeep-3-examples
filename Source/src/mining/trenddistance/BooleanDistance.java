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

/**
 * Trend distance based on a Boolean condition. This example is similar to {@link AverageValueAbsolute}.
 * However, the distance function computes the <em>ratio</em> (instead
 * of the difference) between the reference average and the observed
 * average.
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
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/Division.png" alt="Distance Function"></td>
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
 * 
 * @author Sylvain Hallé
 *
 */
public class BooleanDistance 
{
	public static void main(String[] args)
	{
		
	}
}
