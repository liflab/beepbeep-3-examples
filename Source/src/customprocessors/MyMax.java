package customprocessors;

import java.util.Queue;

import ca.uqac.lif.cep.*;

public class MyMax extends SingleProcessor
{
	Number last = null;

	public MyMax() {
		super(1, 1);
	}

	public boolean compute(Object[] inputs, Queue<Object[]> outputs) {
		Number current = (Number) inputs[0];
		Number output;
		if (last != null) {
			output = Math.max(last.floatValue(), current.floatValue());
			last = current;
			outputs.add(new Object[]{output});
		}
		else {
			last = current;
		}
		return true;
	}

	@Override
	public Processor duplicate() {
		return new MyMax();
	}
}