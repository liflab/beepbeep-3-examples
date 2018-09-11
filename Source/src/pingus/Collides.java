package pingus;

import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.ltl.Troolean;
import ca.uqac.lif.cep.ltl.TrooleanCast;
import ca.uqac.lif.cep.util.Numbers;
import ca.uqac.lif.cep.xml.XPathFunctionGetNumber;

/**
 * Predicate |x<sub>1</sub>-x<sub>2</sub>| &lt; 6 &and; |y<sub>1</sub>-y<sub>2</sub>| < 10 
 */
public class Collides extends FunctionTree
{
  public static final int X_RADIUS = 6;
  public static final int Y_RADIUS = 10;

  public static final transient Collides instance = new Collides();

  ///
  protected Collides()
  {
    super(Troolean.AND_FUNCTION);
    {
      // Build the expression |p1//x - p2//x| < 6
      FunctionTree cast = new FunctionTree(TrooleanCast.instance);
      FunctionTree lt = new FunctionTree(Numbers.isLessThan);
      FunctionTree abs = new FunctionTree(Numbers.absoluteValue);
      FunctionTree minus = new FunctionTree(Numbers.subtraction);
      minus.setChild(0, new XPathFunctionGetNumber("message/characters/character[id=$p1]/position/x/text()"));
      minus.setChild(1, new XPathFunctionGetNumber("message/characters/character[id=$p2]/position/x/text()"));
      abs.setChild(0, minus);
      lt.setChild(0, abs);
      lt.setChild(1, new Constant(X_RADIUS));
      cast.setChild(0, lt);
      setChild(0, cast);
    }
    {
      // Build the expression |p1//y - p2//y| < 10
      FunctionTree cast = new FunctionTree(TrooleanCast.instance);
      FunctionTree lt = new FunctionTree(Numbers.isLessThan);
      FunctionTree abs = new FunctionTree(Numbers.absoluteValue);
      FunctionTree minus = new FunctionTree(Numbers.subtraction);
      minus.setChild(0, new XPathFunctionGetNumber("message/characters/character[id=$p1]/position/y/text()"));
      minus.setChild(1, new XPathFunctionGetNumber("message/characters/character[id=$p2]/position/y/text()"));
      abs.setChild(0, minus);
      lt.setChild(0, abs);
      lt.setChild(1, new Constant(Y_RADIUS));
      cast.setChild(0, lt);
      setChild(1, cast);
    }
  }
  ///

  @Override
  public FunctionTree duplicate(boolean with_state)
  {
    return this;
  }
}