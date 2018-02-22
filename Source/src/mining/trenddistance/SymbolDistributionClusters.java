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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.io.ReadStringStream;
import ca.uqac.lif.cep.peg.TrendDistance;
import ca.uqac.lif.cep.peg.ml.DistanceToClosest;
import ca.uqac.lif.cep.peg.ml.DoublePointCast;
import ca.uqac.lif.cep.peg.MapDistance.ToValueArray;
import ca.uqac.lif.cep.peg.Normalize;
import ca.uqac.lif.cep.tmf.ReplaceWith;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.util.FindPattern;

/**
 * Trend distance based on the statistical distribution of symbols in a
 * stream. In this example, a feature vector is computed from an input
 * trace by calculating the fraction of a's and b's that occur in a sliding
 * window of width 9
 * (see {@link mining.extraction.KmeansSymbolDistribution KmeansSymbolDistribution}
 * for an explanation of how this is computed).
 * The reference pattern is a set of two-dimensional points, corresponding
 * to the centroids of two clusters. The distance function computes the
 * Euclidean distance between the computed feature vector and the
 * <em>closest</em> centroid of the reference set. If this distance is greater
 * than <i>d</i>=0.15, an alarm is raised.
 * <p>
 * For example, suppose that the two centroids have coordinates (0.7, 0.3) and
 * (0.3, 0.7); they are represented by two crosses in the 2D plot below.
 * <p>
 * <img src="{@docRoot}/doc-files/mining/extraction/ClusteringAB.png" alt="Plot">
 * <p>
 * Consider the following window of 9 events:
 * <pre>
 * a, b, a, b, a, b, a, b, a
 * </pre>
 * The feature vector extracted from this window is (0.56, 0.44) (red dot
 * in the plot above). The centroid closest to this point is (0.7, 0.3),
 * but its distance is 0.2, which is greater than 0.15. In that case, the
 * feature vector is considered "too far" from existing clusters, and
 * an alarm is raised.
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
 *   <td><img src="{@docRoot}/doc-files/mining/extraction/SymbolDistributionDoublePoint.png" alt="Processor chain"></td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/PatternParameter.png" alt="Reference Pattern" title="The reference pattern"></td>
 *   <td>{(0.7, 0.3), (0.3, 0.7)}</td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/DistanceFunction.png" alt="Distance Function" title="The function that computes the distance with respect to the reference pattern"></td>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/DistanceToClosest.png" alt="Distance Function">
 *   ({@link ca.uqac.lif.cep.peg.ml.DistanceToClosest DistanceToClosest}
 *   using {@link org.apache.commons.math3.ml.distance.EuclideanDistance EuclideanDistance} metric)</td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/ComparisonFunction.png" alt="Comparison Function" title="The function that compares that distance with a given threshold"></td>
 *   <td><img src="{@docRoot}/doc-files/mining/LessThanOrEqual.png" alt="&leq;"></td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/DistanceThreshold.png" alt="Distance Threshold" title="The distance threshold"></td>
 *   <td>¼</td>
 * </tr>

 * </table>
 * 
 * @author Sylvain Hallé
 *
 */
public class SymbolDistributionClusters 
{
	public static void main(String[] args)
	{
		ReadStringStream reader = new ReadStringStream(SymbolDistributionClusters.class.getResourceAsStream("SymbolDistribution-AB.txt"));
		FindPattern feeder = new FindPattern("(.*?),");
		Connector.connect(reader, feeder);
		/* We then create a processor that computes the feature vector
		 * from an input trace. */
		GroupProcessor vector = new GroupProcessor(1, 1);
		{
			GroupProcessor counter = new GroupProcessor(1, 1);
			{
				ReplaceWith one = new ReplaceWith(new Constant(1));
				counter.associateInput(INPUT, one, INPUT);
				Cumulate sum_one = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
				Connector.connect(one, sum_one);
				counter.associateOutput(OUTPUT, sum_one, OUTPUT);
				counter.addProcessors(one, sum_one);
			}
			Slice slicer = new Slice(new IdentityFunction(1), counter);
			ApplyFunction to_normalized_vector = new ApplyFunction(
					new FunctionTree(DoublePointCast.instance,
					new FunctionTree(Normalize.instance,
					new FunctionTree(ToValueArray.instance, StreamVariable.X))));
			Connector.connect(slicer, to_normalized_vector);
			vector.associateInput(INPUT, slicer, INPUT);
			vector.associateOutput(OUTPUT, to_normalized_vector, OUTPUT);
			vector.addProcessors(slicer, to_normalized_vector);
		}
		Connector.connect(feeder, vector);
		Set<DoublePoint> pattern = new HashSet<DoublePoint>();
		pattern.add(new DoublePoint(new double[]{0.7, 0.3}));
		pattern.add(new DoublePoint(new double[]{0.3, 0.7}));
		TrendDistance<Set<DoublePoint>,Set<DoublePoint>,Number> alarm = new TrendDistance<Set<DoublePoint>,Set<DoublePoint>,Number>(pattern, 9, vector, new FunctionTree(Numbers.absoluteValue, 
				new FunctionTree(new DistanceToClosest(new EuclideanDistance()), StreamVariable.X, StreamVariable.Y)), 0.25, Numbers.isLessThan);
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
