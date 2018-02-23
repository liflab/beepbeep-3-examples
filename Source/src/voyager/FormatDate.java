package voyager;

import ca.uqac.lif.cep.functions.BinaryFunction;

public class FormatDate extends BinaryFunction<Number,Number,Number>
{
	public static final FormatDate instance = new FormatDate();
	
	protected FormatDate()
	{
		super(Number.class, Number.class, Number.class);
	}

	@Override
	public Number getValue(Number x, Number y) 
	{
		return (x.intValue() - 1977) * 365 + y.intValue();
	}
}
