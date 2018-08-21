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
package mining.extraction;

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;

import java.util.ArrayList;
import java.util.Set;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.peg.MapDistance.ToValueArray;
import ca.uqac.lif.cep.peg.Normalize;
import ca.uqac.lif.cep.peg.ProcessorMiningFunction;
import ca.uqac.lif.cep.peg.Sequence;
import ca.uqac.lif.cep.peg.ml.KMeansFunction;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.util.Numbers;
import mining.SequenceReader;

/**
 * Create clusters over the distribution of symbols in a set of input
 * streams.
 * <p>
 * In this example, input traces are made of symbols <tt>a</tt> and
 * <tt>b</tt>. A set of seven such traces is located in the file
 * <tt>strings-1.csv</tt>. For each of these traces, the pattern
 * processor &beta; computes a <em>feature vector</em> made of two numbers,
 * corresponding to the fraction of <tt>a</tt>'s and <tt>b</tt>'s in
 * the trace. This is done by
 * <ul>
 * <li>slicing the input trace according to the current symbol</li>
 * <li>counting the events in each slice</li>
 * <li>taking the values of the resulting map into a list, and normalizing
 * them (so that their sum is equal to 1)</li>
 * </ul>
 * For example, on the input sequence
 * <pre>
 * a, b, a, a, b, b
 * </pre>
 * the resulting feature vector would be (0.4, 0.6).
 * <p>
 * We then use the K-means clustering algorithm to find the centroids of
 * two clusters based on those feature vectors.
 * <p>
 * The processor mining function is therefore parameterized as follows:
 * <p>
 * <table>
 * <tr><th>Parameter</th><th>Value</th></tr>
 * <tr>
 * <td><img src="{@docRoot}/doc-files/mining/trenddistance/BetaProcessor.png" alt="Processor graph"></td>
 * <td><img src="{@docRoot}/doc-files/mining/extraction/SymbolDistributionDoublePoint.png" alt="Processor graph"></td>
 * </tr>
 * <tr>
 * <td><img src="{@docRoot}/doc-files/mining/extraction/AlphaProcessor.png" alt="Processor graph"></td>
 * <td><img src="{@docRoot}/doc-files/mining/extraction/Kmeans-Function.png" alt="Processor graph"> (K-means with K=2)</td>
 * </tr>
 * </table>
 * <p>
 * The traces in the input CSV file either have an approximate 30%-70%
 * distribution of <tt>a</tt>'s and <tt>b</tt>'s, or the reverse. The
 * feature vectors can be plotted as follows, with each dot representing
 * the a-b distribution of a single trace.
 * <p>
 * <img src="{@docRoot}/doc-files/mining/extraction/ClusteringAB.png" alt="Clustering graph">
 * <p>
 * Applying the k-means algorithm, with k=2, will compute two cluster centers,
 * represented by crosses in the above plot.
 * 
 * @author Sylvain Hallé
 *
 */
public class KmeansSymbolDistribution
{
	public static void main(String[] args) throws FunctionException
	{
		/* First, we must get from somewhere a set of sequences. For the sake
		 * of this example, we just create a few dummy sequences of numbers
		 * from the contents of a file. */
		Set<Sequence<String>> sequences = SequenceReader.readStringSequences("strings-1.csv");
		
		/* We then create a processor that computes the feature vector
		 * from an input trace. */
		GroupProcessor vector = new GroupProcessor(1, 1);
		{
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
			ApplyFunction to_normalized_vector = new ApplyFunction(
					new FunctionTree(Normalize.instance,
					new FunctionTree(ToValueArray.instance, StreamVariable.X)));
			Connector.connect(slicer, to_normalized_vector);
			vector.associateInput(INPUT, slicer, INPUT);
			vector.associateOutput(OUTPUT, to_normalized_vector, OUTPUT);
			vector.addProcessors(slicer, to_normalized_vector);
		}
		
		/* Finally, we instantiate a processor mining function, using the
		 * pattern processor defined above, and the K-means clustering
		 * algorithm as the aggregate function. */
		ProcessorMiningFunction<String, ArrayList<?>> pmf = new ProcessorMiningFunction<String,ArrayList<?>>(vector, new ApplyFunction(new KMeansFunction(2)));
		
		/* Let us see the clusters computed by this mining function on the set
		 * of input sequences. There should be two centroids, one roughly
		 * corresponding to a 30-70 distribution of a's vs. b's, and the other
		 * one corresponding to a 70-30 distribution. */
		Set<?> centroids = (Set<?>) pmf.mine(sequences);
		System.out.println(centroids);
	}
}
