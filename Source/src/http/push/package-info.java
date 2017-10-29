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
 * Use HTTP gateways in push mode.
 * <p>
 * In this section, the processor chain operates in <em>push</em> mode.
 * Events are produced upstream, and are pushed to the downstream
 * processors.
 * <p>
 * You are advised to first look at {@link PushLocal}, which contains
 * a single-machine simulation with detailed step-by-step documentation.
 * You can then see the programs {@link PushMachineA} and {@link PushMachineB},
 * which perform the same computation, but across two independent programs
 * that you can run on distinct computers.
 * 
 * @author Sylvain Hallé
 *
 */
package http.push;