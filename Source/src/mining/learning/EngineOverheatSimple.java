/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2019 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mining.learning;

import static ca.uqac.lif.cep.Connector.connect;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IfThenElse;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.graphviz.CallGraphviz;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.peg.forecast.SelfLearningPrediction;
import ca.uqac.lif.cep.peg.weka.UpdateClassifier;
import ca.uqac.lif.cep.peg.weka.WekaUtils;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tuples.FetchAttribute;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Numbers;
import java.io.InputStream;
import plots.BitmapJFrame;
import weka.classifiers.trees.J48;
import weka.core.Attribute;

/**
 * Train a classifier to predict the overheating of an engine.
 * <p>
 * In this example, our input stream is a log of data obtained from a
 * (fake) combustion engine. Each datapoint is made of a timestamp,
 * the current speed (RPM) of the engine, the current gear of the
 * transmission, and the current engine temperature. 
 */
public class EngineOverheatSimple
{
  public static void main(String[] args) throws Exception
  {
    // Read tuples from the file
    InputStream is = EngineOverheatSimple.class.getResourceAsStream("Engine.csv");
    ReadLines reader = new ReadLines(is);
    TupleFeeder tuples = new TupleFeeder();
    connect(reader, tuples);
    
    // The beta processor extracts the value of attributes RPM and Gr
    // and puts them into an array
    ApplyFunction beta = new ApplyFunction(new FunctionTree(
        new Bags.ToArray(Number.class, String.class),
          new FunctionTree(Numbers.numberCast, new FetchAttribute("RPM")),
          new FunctionTree(new FetchAttribute("Gr"), StreamVariable.X)
        ));
    
    // The kappa processor fetches the value of attribute Temp, and
    // checks if it is greater than 60; if so, it returns "overheat",
    // otherwise it returns "normal"
    ApplyFunction kappa = new ApplyFunction(new FunctionTree(IfThenElse.instance,
        new FunctionTree(Numbers.isGreaterThan,
            new FunctionTree(Numbers.numberCast, new FetchAttribute("Temp")),
            new Constant(60)),
        new Constant("overheat"),
        new Constant("normal")
        ));
    
    // The learning attributes for the classifier will be called RPM,
    // Gr and Overheat
    Attribute[] attributes = new Attribute[] {
        new Attribute("RPM"),
        WekaUtils.createAttribute("Gr", "N", "1", "2", "3", "4"),
        WekaUtils.createAttribute("State", "overheat", "normal")
    };
    
    // Update a J48 classifier using input events and these attributes
    UpdateClassifier uc = new UpdateClassifier(new J48(), "Engine", attributes);
    
    // Train a classifier by comparing windows of width 1, offset by 3 events
    SelfLearningPrediction ct = new SelfLearningPrediction(new IdentityFunction(1), beta, 3, 1, kappa, 1, uc);
    connect(tuples, ct);
    
    // Just to be fancy, let's draw the classifier
    ApplyFunction get_graph = new ApplyFunction(WekaUtils.GetGraph.instance);
    connect(ct, get_graph);
    CallGraphviz cg = new CallGraphviz().use(CallGraphviz.Renderer.DOT);
    connect(get_graph, cg);
    
    // Pump the drawing into a widget to display it
    Pump pump = new Pump(500);
    connect(cg, pump);
    BitmapJFrame plot_frame = new BitmapJFrame();
    Connector.connect(pump, plot_frame);
    
    // Let's see what comes out
    plot_frame.start();
    pump.start();
  }
}
