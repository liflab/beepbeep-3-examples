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
		InputStream is = Relationships.class.getResourceAsStream("arf.csv");
		ReadLines reader = new ReadLines(is);
		TupleFeeder tuples = new TupleFeeder();
		Connector.connect(reader, tuples);
		ApplyFunction pairs = new ApplyFunction(new FunctionTree(Bags.product,
				new FunctionTree(new Bags.ToSet(String.class),
						new FunctionTree(new FetchAttribute("author"), StreamVariable.X)),
				new FunctionTree(new Strings.FindRegex("@([_\\d\\w]*)"),
						new FunctionTree(new FetchAttribute("text"), StreamVariable.X))));
		Connector.connect(tuples, pairs);
		Lists.Unpack unpack = new Lists.Unpack();
		Connector.connect(pairs, unpack);
		ApplyFunction explode = new ApplyFunction(new Bags.Explode(String.class, String.class));
		Connector.connect(unpack, explode);
		UpdateGraph graph = new UpdateGraph();
		ApplyFunction td = new ApplyFunction(ToDot.instance);
		Connector.connect(explode, graph, td);
		Pullable p = td.getPullableOutput();
		String dot = "";
		while (p.hasNext())
		{
			dot = (String) p.next();
		}
		System.out.println(dot);
	}
}
