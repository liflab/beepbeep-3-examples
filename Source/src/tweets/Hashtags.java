package tweets;

import java.io.InputStream;
import java.util.Map;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.ReadLines;
import ca.uqac.lif.cep.tmf.Slice;
import ca.uqac.lif.cep.tuples.FetchAttribute;
import ca.uqac.lif.cep.tuples.TupleFeeder;
import ca.uqac.lif.cep.util.Lists;
import ca.uqac.lif.cep.util.Sets;
import ca.uqac.lif.cep.util.Strings;

public class Hashtags 
{
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		InputStream is = Hashtags.class.getResourceAsStream("arf.csv");
		ReadLines reader = new ReadLines(is);
		TupleFeeder tuples = new TupleFeeder();
		Connector.connect(reader, tuples);
		GroupProcessor hts = new GroupProcessor(1, 1);
		{
			ApplyFunction htags = new ApplyFunction(new FunctionTree(
					new Strings.FindRegex("#([_\\d\\w]*)"), 
						new FunctionTree(new FetchAttribute("text"), StreamVariable.X)));
			Lists.Unpack unpack = new Lists.Unpack();
			Sets.PutInto set = new Sets.PutInto();
			Connector.connect(htags, unpack, set);
			hts.addProcessors(htags, unpack, set);
			hts.associateInput(0, htags, 0);
			hts.associateOutput(0, set, 0);
		}
		Slice slice = new Slice(new FetchAttribute("author"), hts);
		Connector.connect(tuples, slice);
		Pullable p = slice.getPullableOutput();
		Map<Object,Object> map = null;
		while (p.hasNext())
		{
			map = (Map<Object,Object>) p.next();
		}
		System.out.println(map);
	}
}
