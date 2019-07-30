package functions.custom;

import java.util.Set;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;

public class CutString extends Function
{
	public void evaluate(Object[] inputs, Object[] outputs, Context c) {
		outputs[0] = ((String) inputs[0]).substring(0, 
		    (Integer) inputs[1]);
	}

	public int getInputArity() {
		return 2;
	}

	public int getOutputArity() {
		return 1;
	}
	
	public Function duplicate(boolean with_state) {
		return new CutString();
	}

	public void getInputTypesFor(Set<Class<?>> s, int i) {
		if (i == 0)
			s.add(String.class);
		if (i == 1)
			s.add(Number.class);
	}

	public Class<?> getOutputTypeFor(int i) {
		if (i == 0)
			return String.class;
		return null;
	}
}
