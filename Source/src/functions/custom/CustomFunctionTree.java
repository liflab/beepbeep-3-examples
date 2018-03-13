package functions.custom;

import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.util.Numbers;

///
public class CustomFunctionTree extends FunctionTree 
{
	public CustomFunctionTree()
	{
		super(Numbers.multiplication,
				new FunctionTree(Numbers.addition, 
						StreamVariable.X, StreamVariable.Y),
				StreamVariable.Z);
	}
}
///
