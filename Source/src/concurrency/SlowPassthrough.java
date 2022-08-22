package concurrency;

import ca.uqac.lif.cep.UtilityMethods;
import ca.uqac.lif.cep.tmf.Passthrough;

public class SlowPassthrough extends Passthrough
{
  public SlowPassthrough(int arity)
  {
    super(arity);
  }
  
  public SlowPassthrough()
  {
    this(1);
  }
  
  @Override
  protected boolean compute(Object[] inputs, Object[] outputs)
  {
    UtilityMethods.pause(1000);
    return super.compute(inputs, outputs);
  }

  @Override
  public SlowPassthrough duplicate(boolean with_state)
  {
    return new SlowPassthrough(getInputArity());
  }
}
