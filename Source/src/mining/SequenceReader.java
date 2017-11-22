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
package mining;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import ca.uqac.lif.cep.peg.Sequence;
import ca.uqac.lif.cep.util.FileHelper;

/**
 * Utility class that creates a set of sequences from a file. This
 * object is used in the code throughout this section in order to
 * avoid programmatically creating input traces for every example.
 *  
 * @author Sylvain Hallé
 */
public class SequenceReader
{
	public static Set<Sequence<Number>> readNumericalSequences(String filename)
	{
		Set<Sequence<Number>> seqs = new HashSet<Sequence<Number>>();
		InputStream is = FileHelper.internalFileToStream(SequenceReader.class, filename);
		if (is != null)
		{
			Scanner scanner = new Scanner(is);
			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine().trim();
				if (line.isEmpty() || line.startsWith("#"))
				{
					continue;
				}
				Sequence<Number> seq = readNumericalSequence(line);
				if (seq != null)
				{
					seqs.add(seq);
				}
			}
			scanner.close();
		}
		return seqs;
	}
	
	public static Set<Sequence<String>> readStringSequences(String filename)
	{
		Set<Sequence<String>> seqs = new HashSet<Sequence<String>>();
		InputStream is = FileHelper.internalFileToStream(SequenceReader.class, filename);
		if (is != null)
		{
			Scanner scanner = new Scanner(is);
			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine().trim();
				if (line.isEmpty() || line.startsWith("#"))
				{
					continue;
				}
				Sequence<String> seq = readStringSequence(line);
				if (seq != null)
				{
					seqs.add(seq);
				}
			}
			scanner.close();
		}
		return seqs;
	}
	
	public static Sequence<Number> readNumericalSequence(String line)
	{
		String[] parts = line.split(",");
		Sequence<Number> seq = new Sequence<Number>();
		for (String p : parts)
		{
			Float f = Float.parseFloat(p.trim());
			seq.add(f);
		}
		return seq;
	}
	
	public static Sequence<String> readStringSequence(String line)
	{
		String[] parts = line.split(",");
		Sequence<String> seq = new Sequence<String>();
		for (String p : parts)
		{
			seq.add(p);
		}
		return seq;
	}
}
