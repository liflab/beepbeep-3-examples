/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2022 Sylvain Hallé

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
package qbf;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.functions.ApplyFunction;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.functions.StreamVariable;
import ca.uqac.lif.cep.io.Print;
import ca.uqac.lif.cep.tmf.Fork;
import ca.uqac.lif.cep.tmf.Pump;
import ca.uqac.lif.cep.tmf.QueueSource;
import ca.uqac.lif.cep.util.Booleans;
import qbf.QuantifiedBooleanVariable.ExistentialVariable;
import qbf.QuantifiedBooleanVariable.UniversalVariable;

import static ca.uqac.lif.cep.Connector.connect;

/**
 * Solves the QBF problem for the formula:
 * <blockquote>
 * &forall;x &exist;y &exist;z : (x &or; y) &and; (&not;x &or; c) &and; (&not;y &or; &not; z)
 * </blockquote>
 * The pipeline to solve this problem is the following:
 * <p>
 * <img src="{@docRoot}/doc-files/qbf/Formula1.png" alt="Pipeline">
 * @author Sylvain Hallé
 */
public class Formula1
{
	public static void main(String[] args)
	{
		/* Create a source that repeatedly alternates between the values true and
		 * false. */
		QueueSource qs = new QueueSource().setEvents(true, false).loop(true);
		
		/* Add a pump pushing events from the source downstream. */
		Pump pump = new Pump();
		Connector.connect(qs, pump);
		
		/* Fork the input and create the processors that enumerate the combinations
		 * of values for each variable. */
		Fork f = new Fork(3);
		connect(pump, f);
		InputVariable x1 = new InputVariable(3, 1);
		InputVariable x2 = new InputVariable(3, 2);
		InputVariable x3 = new InputVariable(3, 3);
		connect(f, 0, x1, 0);
		connect(f, 1, x2, 0);
		connect(f, 2, x3, 0);
		
		/* Connect the inputs to the Boolean function to evaluate. */
		ApplyFunction af = new ApplyFunction(new Expression());
		connect(x1, 0, af, 0);
		connect(x2, 0, af, 1);
		connect(x3, 0, af, 2);
		
		/* Link the output of this formula to the constructs for each quantified
		 * variable, starting from the innermost. */
		ExistentialVariable exists_x3 = new ExistentialVariable();
		connect(af, exists_x3);
		ExistentialVariable exists_x2 = new ExistentialVariable();
		connect(exists_x3, exists_x2);
		UniversalVariable forall_x1 = new UniversalVariable();
		connect(exists_x2, forall_x1);
		
		/* Connect the output of the pipline to a Print processor to display the
		 * result to the standard output. */
		Print p = new Print();
		Connector.connect(forall_x1, p);
		
		/* Since there are 3 variables, turn the crank on the pump 2^3 = 8 times to
		 * enumerate all possible valuations. Turning the crank fewer than 8 times
		 * will result in no output being produced. */
		pump.turn(8);
	}
	
	/**
	 * Representation of the Boolean expression
	 * (x &or; y) &and; (&not;x &or; c) &and; (&not;y &or; &not; z).
	 */
	protected static class Expression extends FunctionTree
	{
		public Expression()
		{
			super(Booleans.and,
					new FunctionTree(Booleans.and,
							new FunctionTree(Booleans.or, StreamVariable.X, StreamVariable.Y),
							new FunctionTree(Booleans.or, 
									new FunctionTree(Booleans.not, StreamVariable.X), StreamVariable.Z)
							),
					new FunctionTree(Booleans.or, 
							new FunctionTree(Booleans.not, StreamVariable.Y),
							new FunctionTree(Booleans.not, StreamVariable.Z))
					);
		}
	}

}
