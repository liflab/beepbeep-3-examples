
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