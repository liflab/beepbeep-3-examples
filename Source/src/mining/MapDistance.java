package mining;

import java.util.HashMap;

import ca.uqac.lif.cep.functions.BinaryFunction;

@SuppressWarnings("rawtypes")
public class MapDistance extends BinaryFunction<HashMap,HashMap,Number>
{
	public static final transient MapDistance instance = new MapDistance();
	
	protected MapDistance()
	{
		super(HashMap.class, HashMap.class, Number.class);
	}

	@Override
	public Number getValue(HashMap x, HashMap y)
	{
		int distance = 0;
		for (Object k: x.keySet())
		{
			int n1 = ((Number) x.get(k)).intValue();
			int n2 = 0;
			if (y.containsKey(k))
			{
				n2 = ((Number) y.get(k)).intValue();
			}
			distance += Math.abs(n1 - n2);
		}
		return distance;
	}

}