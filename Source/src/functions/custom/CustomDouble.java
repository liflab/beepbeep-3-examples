package functions.custom;

import java.util.Set;

import ca.uqac.lif.cep.functions.Function;

public class CustomDouble extends Function
{
	@Override
	public void evaluate(Object[] inputs, Object[] outputs)
	{
		Number n = (Number) inputs[0];
		outputs[0] = n.floatValue() * 2;
	}

	//!
	@Override
	public int getInputArity()
	{
		return 1;
	}

	@Override
	public int getOutputArity() 
	{
		return 1;
	}
	
	@Override
	public Function duplicate(boolean with_state) 
	{
		return new CustomDouble();
	}
	//!

	@Override
	public void getInputTypesFor(Set<Class<?>> s, int i) 
	{
		if (i == 0)
			s.add(Number.class);
	}

	@Override
	public Class<?> getOutputTypeFor(int i) 
	{
		if (i == 0)
			return Number.class;
		return null;
	}

}
