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
 * In this example, we compute twin primes by distributing the computation
 * across two machines over a network.
 * <p>
 * Twin primes are pairs of numbers <i>p</i> and <i>p</i>+2 such that both are
 * prime. For example, (3,5), (11,13) and (17,19) are three such pairs. The
 * <a href="https://en.wikipedia.org/wiki/Twin_prime">twin prime conjecture</a>
 * asserts that there exists an infinity of such pairs.
 * <p>
 * In our setup, Machine A will be programmed to check if each odd number
 * 3, 5, 7, etc. is prime. If so, it will send the number <i>n</i> to Machine B,
 * which will then check if <i>n</i>+2 is prime. If this is the case, Machine B
 * will print to the console the values of <i>n</i> and <i>n</i>+2.
 * <p>
 * The interest of this setup is that checking if a number is prime is an
 * operation that becomes very long for large integers (especially with the
 * algorithm we use here). By having the verification for <i>n</i> and
 * <i>n</i>+2 on two separate machines, the whole processor chain can actually
 * run two primality checks at the same time.
 * <p>
 * Note that this chain of processors is only meant to illustrate a possible
 * use of the HTTP gateways. As such, it is not a very efficient way of finding
 * twin primes: when <i>n</i> and <i>n</i>+2 are both prime, three primality
 * checks will be done: Machine A will first discover that <i>n</i> is prime,
 * which will trigger Machine B to check if <i>n</i>+2 also is. However, since
 * Machine A checks all odd numbers, it will also check for <i>n</i>+2 in its
 * next computation step. Could you think of a better way of using processors
 * to make this more efficient?
 * <p>
 * A few things you might want to try:
 * <ul>
 * <li>Machine B's program depends on the numbers sent by Machine A. Therefore,
 * if you stop Machine A and restart it, you will see Machine B starting the
 * the sequence of twin primes from the beginning.</li> 
 * </ul>
 * @author Sylvain Hallé
 */
package http.push.twinprimes;