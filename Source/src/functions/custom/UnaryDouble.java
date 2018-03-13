package functions.custom;

import ca.uqac.lif.cep.functions.UnaryFunction;

public class UnaryDouble extends UnaryFunction<Number,Number>
{
	public UnaryDouble()
	{
		super(Number.class, Number.class);
	}

	@Override
	public Number getValue(Number x)
	{
		return x.floatValue() * 2;
	}
}
