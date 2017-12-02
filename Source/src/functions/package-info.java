/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2016 Sylvain Hallé

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
 * Create custom {@link Function} objects, directly or by combining existing
 * functions.
 * <p>
 * A particular aspect of BeepBeep 3 is the fact that functions are themselves
 * objects. For example, the {@link ApplyFunction} applies a function (any
 * function) to each incoming event. The actual function that it uses is
 * chosen by passing to this processor a {@link Function} object.
 * <p>
 * BeepBeep and its supplemental palettes already define plenty of functions
 * for specific uses. However, if no function suits your needs, it also
 * provides simple means for creating your own function objects. This can be
 * done in two ways:
 * <ul>
 * <li>By creating a new class that inherits from {@link Function} or one of
 * its descendents</li>
 * <li>By combining (i.e. composing) existing functions in an expression.
 * For example, the function <i>f</i>(<i>x</i>,<i>y</i>) = 2<i>x</i> + <i>y</i>
 * is a function of two arguments, defined by composing existing functions,
 * namely multiplication and addition. In BeepBeep, writing such an expression
 * is done by creating an instance of a {@link FunctionTree}.</li>
 * </ul>
 * @author Sylvain Hallé
 */
package functions;