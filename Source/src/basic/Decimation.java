package basic;

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.LEFT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.RIGHT;
import static ca.uqac.lif.cep.Connector.connect;
import util.UtilityMethods;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.tmf.CountDecimate;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.tmf.Trim;

/**
 * Compute the sum of every pair of successive events. Here we are
 * interested in computing the sum of events at position 0-1, 2-3, 4-5, etc.
 * To do so, we compute first the sum of every two successive events,
 * and then keep every other event of the resulting trace.
 * <p>
 * On the input stream 6, 5, 3, 8, 9, 2, 1, 7, 4, 5, &hellip;,
 * the expected output of this program is:
 * <pre>
 * Event #0 is: 11
 * Event #1 is: 11
 * Event #2 is: 11
 * Event #3 is: 8
 * Event #4 is: 9
 * Event #5 is: 6
 * Event #6 is: 13
 * Event #7 is: 20
 * Event #8 is: 7
 * </pre>
 * 
 * @author Sylvain Hallé
 */
public class Decimation
{
	public static void main(String[] args) throws ConnectorException
	{
		/* Create a stream of dummy values. */
		QueueSource source_values = new QueueSource();
		source_values.setEvents(new Integer[]{6, 5, 3, 8, 9, 2, 1, 7, 4, 5,
				2, 4, 7, 6, 12, 8, 1});
		
		/* Duplicate this stream into two paths. */
		Fork fork = new Fork(2);
		connect(source_values, fork);
		
		/* The first path is plugged directly as the first argument of
		 * an adding processor. */
		FunctionProcessor sum = new FunctionProcessor(Addition.instance);
		connect(fork, LEFT, sum, LEFT);
		
		/* Along the second path, we start by removing the first event of
		 * the stream. This is done using the Trim processor. */
		Trim trim = new Trim(1);
		connect(fork, RIGHT, trim, INPUT);
		
		/* We then plug the output of this Trim processor as the second
		 * argument of the adding processor. This has for effect that the
		 * sum processor will add events of the input stream at positions
		 * i and i+1 (for i = 0, 1, 2, etc.). From the stream of numbers
		 * defined above, the output of this processor will be:
		 * 6+5, 5+3, 3+8, 8+9, etc. */
		connect(trim, OUTPUT, sum, RIGHT);
		
		/* Since we want to keep only the sums of events at positions 0-1,
		 * 2-3, 4-5, etc., we have to remove from this output every other
		 * event. This is done with the CountDecimate processor, which is
		 * instructed to keep every <i>n</i>-th event it receives; here, n=2. */
		CountDecimate decimate = new CountDecimate(2);
		connect(sum, OUTPUT, decimate, INPUT);
		
		/* Get the Pullable of this last processor, and show the first few
		 * values it outputs. */
		Pullable p = decimate.getPullableOutput();
		for (int i = 0; i < 9; i++)
		{
			int v = ((Number) p.pull()).intValue();
			System.out.printf("Event #%d is: %d\n", i, v);
			// Pause between each so you have time to read!
			UtilityMethods.pause(1000);
		}
	}
}
