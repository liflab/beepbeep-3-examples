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
 * Create custom processor objects by creating new classes that
 * inherit from {@link Processor}.
 * <p>
 * Extensibility is a key concept in BeepBeep. Apart from its core, which is
 * made of only a handful of generic classes, all of BeepBeep's functionality
 * is achieved by creating descendents of the core's basic objects.
 * BeepBeep already comes with lots of pre-defined
 * <a href="http://liflab.github.io/beepbeep-3-palettes">palettes</a>; however,
 * if none suits your needs, you can easily create new {@link Processor}
 * objects that perform the desired computation. These new objects can then
 * be connected freely with existing processors.
 * <p>
 * In this section, we give a few simple examples of processor objects that
 * perform special kinds of computation.
 * 
 * @author Sylvain Hallé
 */
package customprocessors;