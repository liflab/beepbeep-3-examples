/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2019 Sylvain Hallé

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
package basic;

import ca.uqac.lif.cep.Adder;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.io.Print;

/**
 * Push events into a processor of input arity 2.
 * The chain of processors in this example can be represented
 * graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/BinaryPush.png" alt="Processor graph">
 * <p>
 * The expected output of this program is:
 * <pre>
 * 0,2,4,6,8,10,12,14,
 * </pre>
 * @see PipingUnaryPush
 * @author Sylvain Hallé
 * @difficulty Easy
 */
public class BinaryPush 
{
	public static void main(String[] args)
	{
		/// We create an instance of the adder processor
		Adder add = new Adder();
		
		// We then create a Print processor, which prints its input
		// to the console
		Print print = new Print().setSeparator("\n");
		
		// We connect the output of add to the input of print
		Connector.connect(add, print);
		
		// We now get a hold of the adder's pushables. Since add is of input
		// arity 2, it has two such pushables, numbered 0 and 1. Notice how
		// we use a different version of getPushableInput, which takes an
		// integer as an argument
		Pushable p0 = add.getPushableInput(0);
		Pushable p1 = add.getPushableInput(1);
		///
		
		//* Let us push an event into p0. Nothing is printed, as the Adder
		// requires one event on each of its input pipes to do something.
		p0.push(3);
		System.out.println("This is the first printed line");
		
		// Let us push an event into p1. Since an event was ready to be
		// consumed at p0, adder can proceed with the addition and push its
		// result to print.
		p1.push(1);
		
		// We now push two events in a row to p1. As above, nothing is printed
		p1.push(4);
		p1.push(1);
		System.out.println("This is the third printed line");
		
		// We push two events to p0. Since events in p1 are ready to be
		// consumed, each push on p0 triggers the evaluation of a new addition
		p0.push(5);
		p0.push(9);
		//*
	}
}
