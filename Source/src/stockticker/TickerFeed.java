package stockticker;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.tmf.Source;
import java.util.Queue;
import java.util.Random;

public class TickerFeed extends Source
{
  public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  protected static final String[] m_realNames = {"MSFT", "GOGL", "APPL", "AMZN", "RHT", "IBM", "DVMT"};

  protected String[] m_symbolNames;

  protected float[] m_lastValues;

  protected Random m_random;

  protected int m_computeCount;
  
  protected int m_maxDays = 0;

  public TickerFeed(int num_companies, int num_days)
  {
    super(1);
    m_random = new Random();
    m_maxDays = num_days;
    m_symbolNames = new String[num_companies];
    m_lastValues = new float[num_companies];
    for (int i = 0; i < num_companies; i++)
    {
      if (i < m_realNames.length)
      {
        m_symbolNames[i] = m_realNames[i];
      }
      else
      {
        m_symbolNames[i] = randomString(4);
      }
      m_lastValues[i] = m_random.nextInt(1000);
    }
    m_computeCount = 0;
  }

  public TickerFeed()
  {
    this(10, 10);
  }

  /**
   * Generates a random string of given length
   * @param length The length of the string
   * @return The string
   */
  protected String randomString(int length)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++)
    {
      sb.append(upper.charAt(m_random.nextInt(25)));
    }
    return sb.toString();
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    int num_day = m_computeCount / m_symbolNames.length;
    if (num_day >= m_maxDays)
    {
      return false;
    }
    int pos = m_computeCount % m_symbolNames.length;
    Object[] out = new Object[3];
    out[0] = num_day;
    out[1] = m_symbolNames[pos];
    float val = m_lastValues[pos] + 30f * (m_random.nextFloat() - 0.5f);
    out[2] = val;
    m_lastValues[pos] = val;
    outputs.add(new Object[] {out});
    m_computeCount++;
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    // Don't need to implement this
    return null;
  }
}
