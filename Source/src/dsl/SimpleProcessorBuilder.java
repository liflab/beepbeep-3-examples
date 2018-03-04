package dsl;

import java.util.ArrayDeque;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Passthrough;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Trim;

public class SimpleProcessorBuilder extends GroupProcessorBuilder
{
	///
	public SimpleProcessorBuilder()
	{
		super();
		try
		{
			setGrammar("<proc>   := <trim> | <decim> | <filter> | <stream> ;\n"
					+ "<trim>   := TRIM <num> FROM ( <proc> );\n"
					+ "<decim>  := KEEP ONE EVERY <num> FROM ( <proc> );\n"
					+ "<filter> := FILTER ( <proc> ) WITH ( <proc> );\n"
					+ "<stream> := INPUT <num> ;\n"
					+ "<num>    := ^[0-9]+;");
		}
		catch (InvalidGrammarException e)
		{
			// Do nothing
		}
	}
	///

	@Builds(rule="<trim>", pop=true, clean=true)
	public Trim handleTrim(Object ... parts)
	{
		Integer n = Integer.parseInt((String) parts[0]);
		Processor p = (Processor) parts[1];
		Trim trim = new Trim(n);
		Connector.connect(p, trim);
		add(trim);
		return trim;
	}

	@Builds(rule="<decim>", pop=true, clean=true)
	public CountDecimate handleDecimate(Object ... parts)
	{
		Integer n = Integer.parseInt((String) parts[0]);
		Processor p = (Processor) parts[1];
		CountDecimate dec = new CountDecimate(n);
		Connector.connect(p, dec);
		add(dec);
		return dec;
	}

	@Builds(rule="<filter>", pop=true, clean=true)
	public Filter handleFilter(Object ... parts)
	{
		Processor p1 = (Processor) parts[0];
		Processor p2 = (Processor) parts[1];
		Filter filter = new Filter();
		Connector.connect(p1, 0, filter, 0);
		Connector.connect(p2, 0, filter, 1);
		add(filter);
		return filter;
	}

	@Builds(rule="<stream>")
	public void handleStream(ArrayDeque<Object> stack)
	{
		Integer n = Integer.parseInt((String) stack.pop());
		stack.pop(); // INPUT
		Passthrough p = forkInput(n);
		stack.push(p);
	}

	public static void main(String[] args) throws ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException
	{
		SimpleProcessorBuilder builder = new SimpleProcessorBuilder();
		{
			System.out.println("First query");
			Processor proc = builder.build("KEEP ONE EVERY 2 FROM (INPUT 0)");
			QueueSource src = new QueueSource().setEvents(0, 1, 2, 3, 4, 5, 6, 8);
			Connector.connect(src, proc);
			Pullable pul1 = proc.getPullableOutput();
			for (int i = 0; i < 5; i++)
				System.out.println(pul1.pull());
		}
		{
			System.out.println("Second query");
			Processor proc = builder.build("KEEP ONE EVERY 2 FROM (TRIM 3 FROM (INPUT 0))");
			QueueSource src = new QueueSource().setEvents(0, 1, 2, 3, 4, 5, 6, 8);
			Connector.connect(src, proc);
			Pullable pul1 = proc.getPullableOutput();
			for (int i = 0; i < 5; i++)
				System.out.println(pul1.pull());
		}
		{
			System.out.println("Third query");
			Processor proc = builder.build("FILTER (INPUT 0) WITH (INPUT 1)");
			QueueSource src0 = new QueueSource().setEvents(0, 1, 2, 3, 4, 5, 6, 8);
			QueueSource src1 = new QueueSource().setEvents(true, false, false, true, true, false);
			Connector.connect(src0, 0, proc, 0);
			Connector.connect(src1, 0, proc, 1);
			Pullable pul1 = proc.getPullableOutput();
			for (int i = 0; i < 5; i++)
				System.out.println(pul1.pull());
		}
	}

}
