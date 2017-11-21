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