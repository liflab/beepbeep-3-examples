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
 * In this section, we show how events can be sent from one processor
 * to another over a network. Thus, a chain of processors can be setup
 * on a first machine, whose output is then relayed to a chain of
 * processors on another machine. This has for effect of <em>distributing</em>
 * the computation of a processor chain across multiple hosts.  
 * <p>
 * There are multiple reasons why one would like to distribute computation.
 * <ul>
 * <li>One machine is made of sensors and has little computing power by
 * itself; it performs basic processing on its events, and relays the rest
 * to another machine to continue the bulk of the computation.</li>
 * <li>Since the communication between the machines is asynchronous, they
 * essentially perform their parts of the computation at the same time.
 * Distributing a processor chain across machines is a cheap way to achieve
 * parallelism.</li>
 * <li>If many parts of a processor chain are memory-intensive, separating
 * them on different machines gives each of them more memory.</li>
 * </ul>
 * <p>
 * In BeepBeep, splitting a processor chain across machines is generally done
 * by combining two palettes:
 * <ul>
 * <li>The <strong>Serialization</strong> palette takes care of transforming
 * arbitrary Java objects (i.e. events) into character strings (for transmission
 * over a network) and back</li>
 * <li>The <strong>Http</strong> palette is responsible for sending and
 * listening to HTTP requests at specific URLs; its processors take care of
 * actually transmitting character strings between two processors over a
 * network</li>
 * </ul>
 * <h3>What's in this package</h3>
 * <p>
 * The examples in this section show a simple form of distribution where two
 * machines (called A and B) communicate across a network. Machine A is the
 * <em>upstream</em> machine, and executes the first part of a processor
 * chain; Machine B is the <em>downstream</em> machine, and receives events
 * from Machine A to continue the computation with further processors.
 * 
 * @author Sylvain Hall�
 *
 */
package http;