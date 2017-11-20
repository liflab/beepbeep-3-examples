
/**
 * Evaluate an event stream based on a distance to a reference trend.
 * <p>
 * <img src="{@docRoot}/doc-files/mining/trenddistance/TrendDistance.png" alt="Processor graph">
 * <p>
 * It is possible, however, to encapsulate this process into a
 * {@link GroupProcessor}, which becomes a generic <em>pattern</em>
 * whose actual computation is based on six parameters.
 * <p>
 * <img src="{@docRoot}/doc-files/mining/trenddistance/TrendDistanceBox.png" alt="Processor graph">
 * <p>
 * Depending on how these six parameters are instantiated, the
 * <tt>TrendDistance</tt> processor computes different things.
 * @author Sylvain Hallé
 *
 */
package mining.trenddistance;