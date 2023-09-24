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
 * Examples of processor chains involving loops, i.e.<!-- --> paths in the
 * graph where the output of a processor ultimately leads back to one of its
 * own inputs.
 * <p>
 * Although loops in processor chains are not explicitly forbidden, they are
 * seldom required in "normal" operations and should be used with extreme care.
 * Connecting the output of a processor directly to its input will very likely
 * result in a {@link StackOverflowError} except in special cases.
 * <p>
 * Nevertheless, there exist situations where such loops are justified and for
 * which BeepBeep's behavior is well defined. This typically occurs when the
 * output of a processor is delayed "forward" in time before going back to its
 * input, for example by the use of an {@link Insert} processor. The examples
 * in this package show some of these situations.
 * 
 * 
 */
package loops;