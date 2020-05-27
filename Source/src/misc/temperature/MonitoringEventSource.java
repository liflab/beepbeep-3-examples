package misc.temperature;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.ProcessorException;
import ca.uqac.lif.cep.tmf.Source;
import java.util.Queue;
import java.util.Random;
import misc.temperature.MonitoringEvent.PowerEvent;
import misc.temperature.MonitoringEvent.TemperatureEvent;

public class MonitoringEventSource extends Source
{
  private final int maxRackId;

  private final long pause;

  private final double temperatureRatio;

  private final double powerStd;

  private final double powerMean;

  private final double temperatureStd;

  private final double temperatureMean;

  private int offset;

  private Random m_random = new Random();

  public MonitoringEventSource(int maxRackId, long pause,
      double temperatureRatio,
      double powerStd,
      double powerMean,
      double temperatureStd,
      double temperatureMean) 
  {
    super(1);
    this.maxRackId = maxRackId;
    this.pause = pause;
    this.temperatureRatio = temperatureRatio;
    this.powerMean = powerMean;
    this.powerStd = powerStd;
    this.temperatureMean = temperatureMean;
    this.temperatureStd = temperatureStd;
  }

  @Override
  protected boolean compute(Object[] inputs, Queue<Object[]> outputs)
  {
    MonitoringEvent monitoringEvent;
    int rackId = m_random.nextInt(maxRackId) + offset;
    if (m_random.nextDouble() >= temperatureRatio)
    {
      double power = m_random.nextGaussian() * powerStd + powerMean;
      monitoringEvent = new PowerEvent(rackId, power);
    }
    else 
    {
      double temperature = m_random.nextGaussian() * temperatureStd + temperatureMean;
      monitoringEvent = new TemperatureEvent(rackId, temperature);
    }
    outputs.add(new Object[] {monitoringEvent});
    try
    {
      Thread.sleep(pause);
    }
    catch (InterruptedException e)
    {
      throw new ProcessorException(e);
    }
    return true;
  }

  @Override
  public Processor duplicate(boolean with_state)
  {
    // TODO Auto-generated method stub
    return null;
  }
}
