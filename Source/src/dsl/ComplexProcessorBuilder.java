package dsl;

import static ca.uqac.lif.cep.Connector.BOTTOM;
import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.LEFT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.RIGHT;
import static ca.uqac.lif.cep.Connector.TOP;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.Builds;
import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.dsl.GroupProcessorBuilder;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.Cumulate;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.functions.TurnInto;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Passthrough;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Numbers;

public class ComplexProcessorBuilder extends GroupProcessorBuilder
{
	public ComplexProcessorBuilder()
	{
		super();
		try
		{
			setGrammar(ComplexProcessorBuilder.class.getResourceAsStream("complex.bnf"));
		}
		catch (InvalidGrammarException e)
		{
			// Do nothing
		}
	}

	//!
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
	//!

	//*
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
	//*

	//@
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
	//@

	//.
	@Builds(rule="<stream>")
	public void handleStream(ArrayDeque<Object> stack)
	{
		Integer n = Integer.parseInt((String) stack.pop());
		stack.pop(); // INPUT
		Passthrough p = forkInput(n);
		add(p);
		stack.push(p);
	}
	//.

	@SuppressWarnings("unchecked")
	///
	@Builds(rule="<apply>", pop=true, clean=true)
	public Processor handleApply(Object ... parts)
	{
		Function f = (Function) parts[0];
		ApplyFunction af = new ApplyFunction(f);
		List<Processor> list = (List<Processor>) parts[1];
		if (list.size() == 1)
		{
			Connector.connect(list.get(0), af);
		}
		else if (list.size() == 2)
		{
			Connector.connect(list.get(0), 0, af, 0);
			Connector.connect(list.get(1), 0, af, 1);
		}
		add(af);
		return af;
	}
	///

	@Builds(rule="<add>")
	public void handleAdd(ArrayDeque<Object> stack)
	{
		Function f2 = (Function) stack.pop();
		Function f1 = (Function) stack.pop();
		stack.pop(); // To remove the "+" symbol
		stack.push(new FunctionTree(Numbers.addition, f1, f2));
	}


	@Builds(rule="<sbt>")
	public void handleSbt(ArrayDeque<Object> stack)
	{
		Function f2 = (Function) stack.pop();
		Function f1 = (Function) stack.pop();
		stack.pop(); // To remove the "-" symbol
		stack.push(new FunctionTree(Numbers.subtraction, f1, f2));
	}

	@Builds(rule="<lt>")
	public void handleLt(ArrayDeque<Object> stack)
	{
		Function f2 = (Function) stack.pop();
		Function f1 = (Function) stack.pop();
		stack.pop(); // To remove the "LT" symbol
		stack.push(new FunctionTree(Numbers.isLessThan, f1, f2));
	}

	@Builds(rule="<abs>")
	public void handleAbs(ArrayDeque<Object> stack)
	{
		Function f1 = (Function) stack.pop();
		stack.pop(); // To remove the "ABS" symbol
		stack.push(new FunctionTree(Numbers.absoluteValue, f1));
	}

	@Builds(rule="<cons>")
	public void handleCons(ArrayDeque<Object> stack)
	{
		stack.push(new Constant(Integer.parseInt((String) stack.pop())));
	}

	//s
	@Builds(rule="<svar>")
	public void handleStreamVariable(ArrayDeque<Object> stack)
	{
		String var_name = (String) stack.pop();
		if (var_name.compareTo("X") == 0)
			stack.push(StreamVariable.X);
		if (var_name.compareTo("Y") == 0)
			stack.push(StreamVariable.Y);
	}
	//s

	//l
	@Builds(rule="<proclist>")
	public void handleProcList(ArrayDeque<Object> stack)
	{
		List<Processor> list = new ArrayList<Processor>();
		stack.pop(); // (
		list.add((Processor) stack.pop());
		stack.pop(); // )
		if (stack.peek() instanceof String && ((String) stack.peek()).compareTo("AND") == 0)
		{
			stack.pop(); // AND
			stack.pop(); // (
			list.add((Processor) stack.pop());
			stack.pop(); // )
		}
		stack.push(list);
	}
	//l

	//a
	@Builds(rule="<avg>", pop=true, clean=true)
	public Processor handleAvg(Object ... parts)
	{
		Fork fork = new Fork(2);
		Connector.connect((Processor) parts[0], fork);
		Cumulate sum_proc = new Cumulate(
				new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(fork, TOP, sum_proc, INPUT);
		TurnInto ones = new TurnInto(1);
		Connector.connect(fork, BOTTOM, ones, INPUT);
		Cumulate counter = new Cumulate(
				new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(ones, OUTPUT, counter, INPUT);
		ApplyFunction division = new ApplyFunction(Numbers.division);
		Connector.connect(sum_proc, OUTPUT, division, LEFT);
		Connector.connect(counter, OUTPUT, division, RIGHT);
		add(fork, sum_proc, ones, counter, division);
		return division;
	}
	//a

	public static void main(String[] args) throws ca.uqac.lif.bullwinkle.ParseTreeObjectBuilder.BuildException
	{
		ComplexProcessorBuilder builder = new ComplexProcessorBuilder();
		/*{
			System.out.println("First query");
			//*
			Processor proc = builder.build("APPLY ABS - X Y ON (INPUT 0) AND (INPUT 1)");
			QueueSource src0 = new QueueSource().setEvents(0, 1, 2, 3, 4);
			QueueSource src1 = new QueueSource().setEvents(5, 6, 8, 2, 5);
			Connector.connect(src0, 0, proc, 0);
			Connector.connect(src1, 0, proc, 1);
			Pullable pul1 = proc.getPullableOutput();
			for (int i = 0; i < 5; i++)
				System.out.println(pul1.pull());
			//*
		}*/
		{
			System.out.println("Second query");
			//*
			Processor proc = builder.build("APPLY + X Y ON (FILTER (INPUT 0) WITH (APPLY LT X 0 ON (INPUT 0))) AND (INPUT 1)");
			QueueSource src0 = new QueueSource().setEvents(0, -1, 2, -3, -4);
			QueueSource src1 = new QueueSource().setEvents(5, 6, 8, 2, 5);
			Connector.connect(src0, 0, proc, 0);
			Connector.connect(src1, 0, proc, 1);
			Pullable pul1 = proc.getPullableOutput();
			for (int i = 0; i < 5; i++)
				System.out.println(pul1.pull());
			//*
		}
	}

}
