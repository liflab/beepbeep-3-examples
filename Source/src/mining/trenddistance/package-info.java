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
 * Evaluate an event stream based on a distance to a reference trend.
 * <p>
 * The examples in this section make use of the <tt>TrendDistance</tt>
 * pattern, illustrated as follows.
 * <p>
 * <img src="{@docRoot}/doc-files/mining/trenddistance/TrendDistance.png" alt="Processor graph">
 * <p>
 * This pattern can be interpreted as follows:
 * <ol>
 * <li>A stream of events is sent into a <tt>Window</tt> processor to keep
 * a suffix of width <tt>n</tt></li>
 * <li>The events of that window are sent to a <em>trend processor</em>, noted
 * by &beta;, which computes a "trend" over that window</li>
 * <li>A <em>reference trend</em> (<tt>P</tt>) is given to a
 * <em>distance function</em> (&delta;) along with the trend computed over
 * the window.</li>
 * <li>The resulting <em>distance</em> is given to a comparison function
 * (<tt>⊑</tt>), which checks if that distance is "smaller" than some given
 * <em>distance threshold</em> (<tt>d</tt>)</li>
 * </ol>
 * It is possible, however, to encapsulate this process into a
 * {@link GroupProcessor}, which becomes a generic <em>pattern</em>
 * whose actual computation is based on six parameters: <tt>n</tt>, &beta;,
 * <tt>P</tt>, &delta;, ⊑, and <tt>d</tt>. 
 * <p>
 * <img src="{@docRoot}/doc-files/mining/trenddistance/TrendDistanceBox.png" alt="Processor graph">
 * <p>
 * Depending on how these six parameters are instantiated, the
 * <tt>TrendDistance</tt> processor computes different things. The examples in
 * this section show different ways of using the <tt>TrendDistance</tt>
 * pattern.
 * 
 * @author Sylvain Hallé
 *
 */
package mining.trenddistance;