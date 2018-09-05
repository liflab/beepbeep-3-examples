package customprocessors;

import ca.uqac.lif.cep.*;
import java.util.Queue;

public class EuclideanDistance extends SynchronousProcessor
{
  public static final EuclideanDistance instance = new EuclideanDistance();
  
  EuclideanDistance() 
  {
    super(2, 1);
  }

  public boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    Point p1 = (Point) inputs[0];
    Point p2 = (Point) inputs[1];
    double distance = Math.sqrt(Math.pow(p2.x - p1.x, 2)
        + Math.pow(p2.y - p1.y, 2));
    outputs.add(new Object[] {distance});
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    return this;
  }
}
