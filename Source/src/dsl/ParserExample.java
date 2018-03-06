/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2018 Sylvain Hallé

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
package dsl;

import java.io.FileNotFoundException;
import java.io.InputStream;

import ca.uqac.lif.bullwinkle.BnfParser;
import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.BnfParser.ParseException;
import ca.uqac.lif.bullwinkle.ParseNode;
import ca.uqac.lif.bullwinkle.ParseNodeVisitor.VisitException;

/**
 * Use the Bullwinkle parser to parse simple arithmetic expressions.
 * <p>
 * <img src="{@docRoot}/doc-files/dsl/tree.png" alt="Parse tree">
 * @author Sylvain Hallé
 */
public class ParserExample 
{
	@SuppressWarnings("unused")
	public static void main(String[] args) throws FileNotFoundException, InvalidGrammarException, ParseException, VisitException 
	{
		///
		InputStream is = ParserExample.class.getResourceAsStream("arithmetic.bnf");
		BnfParser parser = new BnfParser(is);
		ParseNode root = parser.parse("3 + (4 - 5)");
		///
	}

}
