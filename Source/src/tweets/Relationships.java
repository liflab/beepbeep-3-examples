package tweets;

import java.io.InputStream;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.graphviz.ToDot;
import ca.uqac.lif.cep.graphviz.UpdateGraph;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.tuples.FetchAttribute;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.util.Bags;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.Strings;

public class Relationships {

	public static void main(String[] args)
	{
		InputStream is = Relationships.class.getResourceAsStream("file1.csv");
		ReadLines reader = new ReadLines(is);
		TupleFeeder tuples = new TupleFeeder();
		Connector.connect(reader, tuples);
		ApplyFunction pairs = new ApplyFunction(new FunctionTree(Bags.product,
				new FunctionTree(new Bags.ToSet(String.class),
						new FunctionTree(new FetchAttribute("author"), StreamVariable.X)),
				new FunctionTree(new Strings.FindRegex("@(\\w*)"),
						new FunctionTree(new FetchAttribute("text"), StreamVariable.X))));
		Connector.connect(tuples, pairs);
		Lists.Unpack unpack = new Lists.Unpack();
		Connector.connect(pairs, unpack);
		ApplyFunction explode = new ApplyFunction(new Lists.Explode(String.class, String.class));
		Connector.connect(unpack, explode);
		UpdateGraph graph = new UpdateGraph();
		Connector.connect(explode, graph);
		ApplyFunction td = new ApplyFunction(ToDot.instance);
		Connector.connect(graph, td);
		Pullable p = td.getPullableOutput();
		while (p.hasNext())
		{
			System.out.println(p.pull());
		}
	}
}
