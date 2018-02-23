package voyager;

import ca.uqac.lif.cep.functions.UnaryFunction;


/**
 * Converts a string into a number
 *
 */
public class ToSciNumber extends UnaryFunction<Object,Number>
{
	public static final ToSciNumber instance = new ToSciNumber();
	
	protected ToSciNumber() 
	{
		super(Object.class, Number.class);
	}

	@Override
	public Number getValue(Object x)
	{
		if (x instanceof Number)
		{
			return (Number) x;
		}
		if (x instanceof String)
		{
			return Float.valueOf((String) x);
		}
		return 0;
	}
	
}
