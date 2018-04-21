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
 * Allocate multiple threads for the execution of specific parts of a
 * processor chain. This section showcases various processors of the
 * Concurrency palette, which make it possible to encase portions of
 * a processor chain into "wrapper" processors that use multiple threads.
 * <p>
 * Most programs in this section come in pairs: a "sequential" program and a
 * "parallel" program. The two programs perform the same computation, except
 * that the second inserts some thread-aware processors into the chain. These
 * programs generally run a CPU-intensive algorithm (such as computing the
 * n-th Fibonacci number). The actual computation does not really matter;
 * it is only meant to illustrate how the two programs handle the same
 * computing load. 
 * <p>
 * Examples in this section:
 * <ul>
 * <li>Use of a {@link ca.uqac.lif.cep.concurrency.PushPipeline PushPipeline}
 *   to push events to a stateless processor in parallel 
 *   ({@link FibonacciSequential} and {@link FibonacciParallel})</li>
 * <li>Parallelism for sliding window computations
 *   ({@link WindowSequential} and {@link WindowParallel})</li>
 * </ul>
 * @author Sylvain Hallé
 */
package concurrency;