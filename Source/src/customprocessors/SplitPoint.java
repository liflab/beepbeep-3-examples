package customprocessors;

import ca.uqac.lif.cep.*;
import java.util.Queue;

public class SplitPoint extends SynchronousProcessor 
{
  public SplitPoint() 
  {
    super(1, 2);
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    Point p = (Point) inputs[0];
    outputs.add(new Object[] {p.x, p.y});
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    return new SplitPoint();
  }
}
