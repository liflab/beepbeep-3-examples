package pingus;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.xml.XPathFunctionGetNumber;

/**
 * Function |x<sub>1</sub>-x<sub>2</sub>|
 */
public class DistanceX extends FunctionTree
{
  public static final transient DistanceX instance = new DistanceX();
  
  ///
	protected DistanceX()
	{
		super(Numbers.absoluteValue);
		FunctionTree minus = new FunctionTree(Numbers.subtraction);
		minus.setChild(0, new XPathFunctionGetNumber("message/characters/character[id=$p1]/position/x/text()"));
		minus.setChild(1, new XPathFunctionGetNumber("message/characters/character[id=$p2]/position/x/text()"));
		setChild(0, minus);
	}
	///
	
	@Override
	public DistanceX clone()
	{
		DistanceX dx = new DistanceX();
		return dx;
	}
	
	@Override
	public DistanceX duplicate(boolean with_state)
	{
		return this;
	}
	
	@Override
	public void evaluate(Object[] inputs, Object[] outputs, Context context)
	{
		super.evaluate(inputs, outputs, context);
	}
}