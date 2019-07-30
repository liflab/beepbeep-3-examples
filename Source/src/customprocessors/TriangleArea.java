package customprocessors;

import ca.uqac.lif.cep.Context;
import ca.uqac.lif.cep.functions.Function;
import java.util.Set;

public class TriangleArea extends Function
{
  public TriangleArea()
  {
    super();
  }

  @Override
  public void evaluate(Object[] inputs, Object[] outputs, Context con)
  {
    float a = ((Number) inputs[0]).floatValue();
    float b = ((Number) inputs[1]).floatValue();
    float c = ((Number) inputs[2]).floatValue();
    float s = (a + b + c) / 2f;
    outputs[0] = Math.sqrt(s * (s-a) * (s-b) * (s-c));
  }
  
  @Override
  public boolean evaluatePartial(Object[] inputs, 
      Object[] outputs, Context c)
  {
    if (inputs[0] != null && ((Number) inputs[0]).floatValue() == 0)
    {
      outputs[0] = 0;
      return true;
    }
    if (inputs[1] != null && ((Number) inputs[1]).floatValue() == 0)
    {
      outputs[0] = 0;
      return true;
    }
    if (inputs[2] != null && ((Number) inputs[2]).floatValue() == 0)
    {
      outputs[0] = 0;
      return true;
    }
    if (inputs[0] != null && inputs[1] != null && inputs[2] != null)
    {
      evaluate(inputs, outputs);
      return true;
    }
    outputs[0] = null;
    return false;
  }

  @Override
  public int getInputArity()
  {
    return 3;
  }

  @Override
  public int getOutputArity()
  {
    return 1;
  }

  @Override
  public void getInputTypesFor(Set<Class<?>> classes, int index)
  {
    classes.add(Number.class);
  }

  @Override
  public Class<?> getOutputTypeFor(int index)
  {
    return Number.class;
  }

  @Override
  public Function duplicate(boolean with_state)
  {
    return new TriangleArea();
  }
  
  public static void main(String[] args)
  {
    ///
    TriangleArea ta = new TriangleArea();
    Object[] out = new Object[1];
    boolean b;
    b = ta.evaluatePartial(new Object[] {3, 4, 5}, out, null);
    System.out.println("b: " + b + ", " + out[0]);  
    b = ta.evaluatePartial(new Object[] {3, null, 5}, out, null);
    System.out.println("b: " + b + ", " + out[0]);
    b = ta.evaluatePartial(new Object[] {3, null, 0}, out, null);
    System.out.println("b: " + b + ", " + out[0]);
    ///
  }
}
