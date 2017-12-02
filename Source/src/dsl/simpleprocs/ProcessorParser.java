package dsl.simpleprocs;

import java.util.Stack;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.tmf.ReplaceWith;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.mtnp.util.FileHelper;

public class ProcessorParser extends ExpressionParser<GroupProcessor>
{
	protected int m_inArity;
	protected int m_outArity;
	
	public ProcessorParser setArity(int in, int out)
	{
		m_inArity = in;
		m_outArity = out;
		return this;
	}

	@Override
	protected void preVisit()
	{
		m_builtObject = new GroupProcessor(m_inArity, m_outArity);
	}

	@Override
	protected GroupProcessor postVisit(Stack<Object> stack) 
	{
		Processor p = (Processor) stack.peek();
		m_builtObject.associateOutput(0, p, 0);
		return m_builtObject;
	}

	@Override
	protected String getGrammar() 
	{
		return FileHelper.internalFileToString(ProcessorParser.class, "processors.bnf");
	}

	protected void doCountDecimate(Stack<Object> stack)
	{
		Processor p = (Processor) stack.pop();
		stack.pop(); // from
		Constant n = (Constant) stack.pop();
		stack.pop(); // every
		stack.pop(); // one
		stack.pop(); // Take
		CountDecimate dec = new CountDecimate(((Number) n.getValue()).intValue());
		Connector.connect(p, dec);
		m_builtObject.addProcessor(dec);
		stack.push(dec);
	}
	
	protected void doMutator(Stack<Object> stack)
	{
		Number n = (Number) stack.pop();
		stack.pop(); // into
		Processor p = (Processor) stack.pop();
		stack.pop(); // Turn
		ReplaceWith mutator = new ReplaceWith(new Constant(n));
		Connector.connect(p, mutator);
		m_builtObject.addProcessor(mutator);
		stack.push(mutator);
	}
	
	public void doNumber(Stack<Object> stack)
	{
		String s = (String) stack.pop();
		Integer i = Integer.parseInt(s);
		stack.push(new Constant(i));
	}
	
	protected void doCumulativeSum(Stack<Object> stack)
	{
		Processor p = (Processor) stack.pop();
		stack.pop(); // Accumulate
		CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Numbers.addition));
		Connector.connect(p, sum);
		m_builtObject.addProcessor(sum);
		stack.push(sum);
	}
	
	protected void doQueueSource(Stack<Object> stack)
	{
		stack.pop(); // ]
		Number n3 = ((Number) ((Constant) stack.pop()).getValue()).intValue();
		stack.pop(); // ,
		Number n2 = ((Number) ((Constant) stack.pop()).getValue()).intValue();
		stack.pop(); // ,
		Number n1 = ((Number) ((Constant) stack.pop()).getValue()).intValue();
		stack.pop(); // [
		QueueSource source = new QueueSource();
		source.addEvent(n1).addEvent(n2).addEvent(n3);
		stack.push(source);
		m_builtObject.addProcessor(source);
	}
}
