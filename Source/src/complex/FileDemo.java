/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2023 Sylvain Hallé

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
package complex;

import static ca.uqac.lif.cep.Connector.connect;

import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pushable;
import ca.uqac.lif.cep.complex.RangeCep;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.Print.Println;
import ca.uqac.lif.cep.tmf.FilterOn;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Freeze;
import ca.uqac.lif.cep.tmf.Insert;
import ca.uqac.lif.cep.tmf.SliceLast;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Booleans;
import ca.uqac.lif.cep.util.Equals;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.NthElement;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Creates complex events out of low-level events occurring to files.
 * Each basic event represents an operation done on a Java iterator. It is
 * an array made of at least two elements:
 * <ul>
 * <li>The name of the file being manipulated (a String)</li>
 * <li>The name of the operation being done on this file (a String)</li>
 * </ul>
 * <p>
 * The operation can be either "open", "close", "read" or "write". When the
 * operation is "write" or "read", the array contains two more elements:
 * <ul>
 * <li>The offset at which bytes are written (resp. read)</li>
 * <li>The number of bytes being written (resp. read)</li>
 * </ul>
 * <p>
 * The goal is to produce a high-level stream where each event summarizes the
 * "lifecycle" of a file. When a file is closed, a "complex"
 * {@link FileOperation} event should be produced, summarizing the operations
 * done on this file during the time it was open:
 * <ul>
 * <li>The filename</li>
 * <li>Whether the interaction was for reading or for writing</li>
 * <li>The number of bytes read (or written)</li>
 * <li>Whether the operations were <em>contiguous</em>. Contiguous operations
 * mean that the first read (resp. write) operation occurs at the start of the
 * file (offset 0), and that the next operation starts where the previous one
 * left off.</li>
 * <li>The global range of bytes that was accessed by this interaction. This
 * is calculated as the interval from the minimum value of <i>offset</i> seen
 * in an operation, to the maximum value of <i>offset</i>+<i>length</i>.
 * </li>
 * </ul>
 * <p>
 * The pipeline allows resets, which means that if the same file is reopened
 * at a later time, a new complex event will eventually be output for this new
 * interaction.
 * <p>
 * The global pipeline is illustrated as follows:
 * <p>
 * <img src={@docRoot}/doc-files/complex/FileAccess.png" alt="Pipeline" />
 */
public class FileDemo
{
	public static void main(String[] args)
	{
		/* Create a processor that gets the filename from the first event. */
		GroupProcessor get_filename = new GroupProcessor(1, 1) {{
			ApplyFunction a = new ApplyFunction(new NthElement(0));
			Freeze f = new Freeze();
			connect(a, f);
			addProcessors(a, f).associateInput(a).associateOutput(f);
		}};
		
		/* Create a processor that gets the access mode (i.e. read or write) first
		 * such event. */
		GroupProcessor read_or_write = new GroupProcessor(1, 1) {{
			ApplyFunction op = new ApplyFunction(new NthElement(1));
			FilterOn filter = new FilterOn(new FunctionTree(Booleans.or,
					new FunctionTree(Equals.instance, StreamVariable.X, new Constant("read")),
					new FunctionTree(Equals.instance, StreamVariable.X, new Constant("write"))
			));
			Freeze f = new Freeze();
			connect(op, filter, f);
			addProcessors(op, filter, f).associateInput(op).associateOutput(f);
		}};
		
		/* Create a processor that sums the bytes written or read. */
		GroupProcessor total_bytes = new GroupProcessor(1, 1) {{			
			FilterOn filter = new FilterOn(new FunctionTree(Booleans.or,
					new FunctionTree(Equals.instance, new FunctionTree(new NthElement(1), StreamVariable.X), new Constant("read")),
					new FunctionTree(Equals.instance, new FunctionTree(new NthElement(1), StreamVariable.X), new Constant("write"))
			));
			ApplyFunction bytes = new ApplyFunction(new FunctionTree(Numbers.numberCast, new NthElement(3)));
			Cumulate sum = new Cumulate(new CumulativeFunction<Number>(Numbers.addition));
			connect(filter, bytes, sum);
			addProcessors(filter, bytes, sum).associateInput(filter).associateOutput(sum);
		}};
		
		/* Create a processor that checks if all accesses are contiguous. */
		GroupProcessor contiguous = new GroupProcessor(1, 1) {{
			FilterOn filter = new FilterOn(new FunctionTree(Booleans.or,
					new FunctionTree(Equals.instance, new FunctionTree(new NthElement(1), StreamVariable.X), new Constant("read")),
					new FunctionTree(Equals.instance, new FunctionTree(new NthElement(1), StreamVariable.X), new Constant("write"))
			));
			Fork f = new Fork();
			connect(filter, f);
			ApplyFunction cur_offset = new ApplyFunction(new NthElement(2));
			connect(f, 0, cur_offset, 0);
			ApplyFunction exp_offset = new ApplyFunction(new FunctionTree(Numbers.addition, new NthElement(2), new NthElement(3)));
			connect(f, 1, exp_offset, 0);
			Insert ins = new Insert(1, 0);
			connect(exp_offset, ins);
			ApplyFunction eq = new ApplyFunction(Equals.instance);
			connect(cur_offset, 0, eq, 0);
			connect(ins, 0, eq, 1);
			Cumulate and = new Cumulate(new CumulativeFunction<Boolean>(Booleans.and));
			connect(eq, and);
			addProcessors(filter, f, cur_offset, exp_offset, ins, eq, and).associateInput(filter).associateOutput(and);
		}};
		
		/* Create a processor that gets the range of bytes accessed. */
		GroupProcessor byte_range = new GroupProcessor(1, 1) {{
			FilterOn filter = new FilterOn(new FunctionTree(Booleans.or,
					new FunctionTree(Equals.instance, new FunctionTree(new NthElement(1), StreamVariable.X), new Constant("read")),
					new FunctionTree(Equals.instance, new FunctionTree(new NthElement(1), StreamVariable.X), new Constant("write"))
			));
			Fork f = new Fork();
			connect(filter, f);
			ApplyFunction off = new ApplyFunction(new FunctionTree(Numbers.numberCast, new NthElement(2)));
			connect(f, 0, off, 0);
			ApplyFunction off_len = new ApplyFunction(new FunctionTree(Numbers.addition, new NthElement(2), new NthElement(3)));
			connect(f, 1, off_len, 0);
			Cumulate min = new Cumulate(new CumulativeFunction<Number>(Numbers.minimum));
			connect(off, min);
			Cumulate max = new Cumulate(new CumulativeFunction<Number>(Numbers.maximum));
			connect(off_len, max);
			ApplyFunction pair = new ApplyFunction(new Bags.ToList(2));
			connect(min, 0, pair, 0);
			connect(max, 0, pair, 1);
			addProcessors(filter, f, off, off_len, min, max, pair).associateInput(filter).associateOutput(pair);
		}};
		
		/* Create a RangeCep processor that will aggregate information about a
		 * operations done on a file. */
		RangeCep cep = new RangeCep(
				new ApplyFunction(new FunctionTree(Booleans.not, new FunctionTree(Equals.instance, new FunctionTree(new NthElement(1), StreamVariable.X), new Constant("close")))), 
				new Processor[] {get_filename, read_or_write, total_bytes, contiguous, byte_range}, 
				new Bags.ToArray(5)).allowRestarts(true);
		
		/* Create a slice processor associating one instance of the RangeCep
		 * processor to each individual instance of file in circulation. */
		SliceLast slice = new SliceLast(new NthElement(0), cep);
		
		/* Connect to a printer and push some events. */
		connect(slice, new Lists.Unpack(), new Println());
		Pushable p = slice.getPushableInput();
		p.push(new Object[] {"foo.txt", "open"});
		p.push(new Object[] {"foo.txt", "read", 0, 10});
		p.push(new Object[] {"bar.txt", "open"});
		p.push(new Object[] {"foo.txt", "read", 9, 12});
		p.push(new Object[] {"bar.txt", "write", 0, 33});
		p.push(new Object[] {"bar.txt", "write", 33, 2});
		p.push(new Object[] {"foo.txt", "read", 12, 5});
		p.push(new Object[] {"bar.txt", "close"});
		p.push(new Object[] {"foo.txt", "close"});
		p.push(new Object[] {"bar.txt", "open"});
		p.push(new Object[] {"bar.txt", "write", 10, 4});
		p.push(new Object[] {"bar.txt", "close"});
	}
}
