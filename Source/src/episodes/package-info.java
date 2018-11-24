/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
 * Examples related to a stream of events for a pressure sensor. This stream of
 * events is made of raw pressure readings made by the sensor, and is separated
 * into various parts using special events that act as markers.
 * <em>Episodes</em> are sub-sequences of successive pressure readings, and
 * <em>days</em> are sequences of episodes. 
 * <p>
 * Formally, a {@link PressureEvent} can be either:
 * <ul>
 * <li>A pressure reading</li>
 * <li>A marker indicating the end of a day, represented by the symbol ✸</li>
 * <li>A marker indicating the start of an <em>episode</em>, represented by the
 * symbol ↑</li>
 * <li>A marker indicating the end of an <em>episode</em>, represented by the
 * symbol ↓</li>
 * </ul>
 * For example, given this stream of pressure events:
 * <blockquote>
 * ✸ ↑ 151 142 ↓ ↑ 148 149 144 ↓ ✸ ↑ 150 142 ↓ ✸
 * </blockquote>
 * one can identify two days; Day 1 is composed of two episodes (one of two
 * readings and one of three readings), and Day 2 is composed of one episode of
 * two readings.
 * <p>
 * The examples in this section illustrate the possibility of computing
 * "hierarchical" summaries on parts of the input stream; two examples are
 * included:
 * <ul>
 * <li>{@link MaxEpisode}: compute the maximum value inside each episode</li>
 * <li>{@link AverageEpisodes}: compute the average of each episode, and
 * group these averages into days</li>
 * </ul>
 * <p>
 * In BeepBeep, such hierarchies can be obtained by nesting {@link Slice} or
 * {@link ResetLast} processors.
 * The notion of hierarchical summary has been discussed, among others, in
 * the following publication:
 * <blockquote>
 * K. Mamouras, M. Raghothaman, R. Alur, Z.G. Ives, S. Khanna. (2017).
 * StreamQRE: modular specification and efficient evaluation of quantitative 
 * queries over streaming data. <i>Proc. PLDI 2017</i>, ACM.
 * DOI: <tt>10.1145/3062341.3062369</tt>.
 * </blockquote>
 */
package episodes;