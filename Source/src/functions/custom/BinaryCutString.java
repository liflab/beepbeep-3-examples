package functions.custom;

import ca.uqac.lif.cep.functions.BinaryFunction;

public class BinaryCutString extends 
  BinaryFunction<String,Number,String> 
{
	public BinaryCutString()
	{
		super(String.class, Number.class, String.class);
	}

	public String getValue(String s, Number n)
	{
		return s.substring(0, n.intValue());
	}
}
