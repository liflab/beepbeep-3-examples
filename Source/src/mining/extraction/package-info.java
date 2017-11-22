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

/**
 * Extract trends and patterns from a set of input streams.
 * <p>
 * This is accomplished by an instance of Pat The Miner's
 * <tt>SetMiningFunction</tt>. This function takes as its input a set of
 * sequences, and returns a "trend" computed on these sequences. A trend
 * can potentially be anything, such an average, a vector of features,
 * a statistical distribution, etc.
 * <p>
 * Although Pat The Miner provides a few built-in functions (and the
 * possibility for a user to create their own by extending
 * <tt>SetMiningFunction</tt>), the <tt>ProcessorMiningFunction</tt> object
 * simplifies the task of creating mining functions through the use of
 * BeepBeep processors. The <tt>ProcessorMiningFunction</tt>
 * takes as input as set of sequences,
 * and produces as output a "pattern" object, representing a trend computed
 * from the contents of the input sequences.
 * <p>
 * When created, the <tt>ProcessorMiningFunction</tt> is parameterized by two
 * BeepBeep <tt>Processor</tt> objects:
 * <table>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/trenddistance/BetaProcessor.png" alt="Processor graph"></td>
 *   <td>This processor, called the <em>trend processor</em>, computes a trend
 *   from a single input sequence. The trend is taken as the last event output
 *   by &beta; when being fed a sequence of events.</td>
 * </tr>
 * <tr>
 *   <td><img src="{@docRoot}/doc-files/mining/extraction/AlphaProcessor.png" alt="Processor graph"></td>
 *   <td>This processor, called the <em>aggregation processor</em>, aggregates
 *   the trends computed by &beta; from each input sequence into a single,
 *   "aggregated" trend. Its input is an array of values.
 * </tr>
 * </table>
 * In addition, the computation of &beta;'s output on each input sequence can
 * be done in parallel in different threads, if one supplies a properly
 * configured {@link ThreadManager} to the function.
 * <p>
 * Depending on how these two parameters are instantiated, the
 * <tt>ProcessorMiningFunction</tt> processor computes different things. The examples in
 * this section show different ways of using the mining functions, an in
 * particular the <tt>ProcessorMiningFunction</tt> object.
 * @author Sylvain Hallé
 *
 */
package mining.extraction;