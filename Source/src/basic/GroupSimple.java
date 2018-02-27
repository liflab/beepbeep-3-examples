package basic;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Trim;
import ca.uqac.lif.cep.util.Numbers;

/**
 * Encapsulate a chain of processors into a
 * {@link ca.uqac.lif.cep.Group Group}.
 * The chain of processors in this example can be
 * represented graphically as:
 * <p>
 * <img src="{@docRoot}/doc-files/basic/GroupSimple.png" alt="Processor graph">
 * @see SumTwo
 * @author Sylvain Hallé
 * @difficulty Easy
 * {@link GroupProcessor#associateInput(int, ca.uqac.lif.cep.Processor, int) 
 */
public class GroupSimple 
{
	public static void main(String[] args)
	{
		/// Create a source of arbitrary numbers
		QueueSource source = new QueueSource().setEvents(1, 2, 3, 4, 5, 6);

		// Fork the stream in two and connect it to the source
		GroupProcessor group = new GroupProcessor(1, 1);
		{
			Fork fork = new Fork(2);
			ApplyFunction add = new ApplyFunction(Numbers.addition);
			Connector.connect(fork, 0, add, 0);
			Trim trim = new Trim(1);
			Connector.connect(fork, 1, trim, 0);
			Connector.connect(trim, 0, add, 1);
			group.addProcessors(fork, trim, add);
			group.associateInput(0, fork, 0);
			group.associateOutput(0, add, 0);
		}
		
		// Connect the source to the group
		Connector.connect(source, group);

		/* Let us now print what we receive by pulling on the output of
		 * group. */
		Pullable p = group.getPullableOutput();
		for (int i = 0; i < 6; i++)
		{
			float x = (Float) p.pull();
			System.out.println("The event is: " + x);
		}
		///
	}
}
