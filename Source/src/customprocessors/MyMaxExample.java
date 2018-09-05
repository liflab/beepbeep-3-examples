package customprocessors;

import ca.uqac.lif.cep.tmf.QueueSource;

public class MyMaxExample
{

  public static void main(String[] args)
  {
    QueueSource src = new QueueSource();
    src.setEvents(1, 2, 1);
    
  }

}
