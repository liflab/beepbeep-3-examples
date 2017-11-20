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
}
