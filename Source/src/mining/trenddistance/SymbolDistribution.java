/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

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
package mining.trenddistance;

import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;

import java.util.HashMap;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.GroupProcessor;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.functions.ArgumentPlaceholder;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.ConstantProcessor;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.IdentityFunction;
import ca.uqac.lif.cep.input.CsvFeeder;
import ca.uqac.lif.cep.io.StreamReader;
import ca.uqac.lif.cep.numbers.AbsoluteValue;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.numbers.IsLessThan;
import ca.uqac.lif.cep.peg.TooFarFromTrend;
import ca.uqac.lif.cep.tmf.SlicerMap;
import ca.uqac.lif.cep.util.FileHelper;
import mining.MapDistance;

/**
 * Alert when the statistical distribution of symbols is too different
 * from a reference distribution.
 * <p>
 * 
 * <img src="{@docRoot}/doc-files/mining/SymbolDistribution-PatternProcessor.png" alt="Pattern processor">
 * @author Sylvain Hallé
 *
 */
public class SymbolDistribution 
{
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws ConnectorException
	{
		StreamReader reader = new StreamReader(FileHelper.internalFileToStream(SymbolDistribution.class, "SymbolDistribution.txt"));
		CsvFeeder feeder = new CsvFeeder();
		Connector.connect(reader, feeder);
		GroupProcessor counter = new GroupProcessor(1, 1);
		{
			ConstantProcessor one = new ConstantProcessor(new Constant(1));
			counter.associateInput(INPUT, one, INPUT);
			CumulativeProcessor sum_one = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			Connector.connect(one, sum_one);
			counter.associateOutput(OUTPUT, sum_one, OUTPUT);
			counter.addProcessors(one, sum_one);
		}
		SlicerMap slicer = new SlicerMap(new IdentityFunction(1), counter);
		HashMap<String,Integer> pattern = new HashMap<String,Integer>();
		pattern.put("a", 6);
		pattern.put("b", 1);
		pattern.put("c", 2);
		TooFarFromTrend<HashMap,Number,Number> alarm = new TooFarFromTrend<HashMap,Number,Number>(pattern, 9, slicer, new FunctionTree(AbsoluteValue.instance, 
				new FunctionTree(MapDistance.instance, new ArgumentPlaceholder(0), new ArgumentPlaceholder(1))), 2, IsLessThan.instance);
		Connector.connect(feeder, alarm);
		Pullable p = alarm.getPullableOutput();
		boolean b = true;
		for (int i = 0; b && i < 10; i++)
		{
			b = (Boolean) p.pull();
			System.out.println(b);
		}
	}
}
