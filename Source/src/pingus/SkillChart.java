package pingus;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.tmf.Window;
import ca.uqac.lif.cep.util.FindPattern;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.Multiset;
import ca.uqac.lif.cep.xml.ParseXml;
import ca.uqac.lif.cep.xml.XPathFunction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SkillChart
{
  public static void main(String[] args) throws FileNotFoundException
  {
    InputStream is = new FileInputStream(new File("/home/sylvain/Workspaces/beepbeep/crv2016/Source/Pingus/traces/pingus/simple-collides-ok.xml"));
    ReadLines s_reader = new ReadLines(is);
    FindPattern feeder = new FindPattern("(<message>.*?</message>)");
    ApplyFunction x_reader = new ApplyFunction(ParseXml.instance);
    Connector.connect(s_reader, feeder, x_reader);
    ///
    ApplyFunction skill = new ApplyFunction(new XPathFunction("message/characters/character/status/text()"));
    Connector.connect(x_reader, skill);
    GroupProcessor gp = new GroupProcessor(1, 1);
    {
      Lists.Unpack unpack = new Lists.Unpack();
      Multiset.PutInto pi = new Multiset.PutInto();
      Connector.connect(unpack, pi);
      gp.addProcessors(unpack, pi);
      gp.associateInput(0, unpack, 0);
      gp.associateOutput(0, pi, 0);
    }
    Window win = new Window(gp, 2);
    Connector.connect(skill, win);
    ///
    Pullable p = win.getPullableOutput();
    while (p.hasNext())
    {
      System.out.println(p.pull());
    }
  }
}
