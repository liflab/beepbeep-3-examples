package customprocessors;

import java.util.LinkedList;
import java.util.Queue;

import ca.uqac.lif.cep.*;

public class Stuttering extends SingleProcessor {

	public Stuttering() {
		super(1, 1);
	}

	public boolean compute(Object[] inputs, Queue<Object[]> outputs) {
		Number n = (Number) inputs[0];
		Queue<Object[]> queue = new LinkedList<Object[]>();
		for (int i = 0; i < n.intValue(); i++) {
			queue.add(inputs);
		}
		outputs.add(new Object[]{queue});
		return true;
	}

	@Override
	public Processor duplicate() {
		return new Stuttering();
	}
}
