package customprocessors;

import java.util.Queue;

import ca.uqac.lif.cep.*;

public class OutIfPositive extends SingleProcessor {

	public OutIfPositive() {
		super(1, 1);
	}

	public boolean compute(Object[] inputs, Queue<Object[]> outputs) {
		Number n = (Number) inputs[0];
		if (n.floatValue() > 0)
			outputs.add(new Object[]{n});
		return true;
	}

	@Override
	public Processor duplicate() {
		return new OutIfPositive();
	}
}