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
package concurrency;

import java.math.BigDecimal;
import java.math.BigInteger;

import ca.uqac.lif.cep.functions.BinaryFunction;

/**
 * Utility class to manipulate Java's {@link BigInteger} objects. Since the
 * processor chains in these examples manipulate potentially large numbers,
 * the standard {@code long} type is not sufficient to hold their value.  
 * @author Sylvain Hallé
 */
public class BigIntegers
{
	/**
	 * Instance of the addition function on {@link BigInteger}s
	 */
	public static final BigIntegerAddition addition = BigIntegerAddition.instance;
	
	/**
	 * Implementation of the addition function on {@link BigInteger}s
	 */
	public static class BigIntegerAddition extends BinaryFunction<BigInteger,BigInteger,BigInteger>
	{
		/**
		 * This class is a singleton
		 */
		public static final BigIntegerAddition instance = new BigIntegerAddition();
		
		private BigIntegerAddition()
		{
			super(BigInteger.class, BigInteger.class, BigInteger.class);
		}
		
		@Override
		public BigInteger getStartValue()
		{
			return BigInteger.ZERO;
		}

		@Override
		public BigInteger getValue(BigInteger x, BigInteger y)
		{
			return x.add(y);
		}
		
		@Override
		public BigIntegerAddition duplicate(boolean with_state)
		{
			return instance;
		}
	}
	
	/**
	 * Computes the square root of a big decimal
	 * @param n The number
	 * @return The square root of this number, rounded to the
	 * nearest integer
	 */
	public static BigInteger sqrt(BigInteger n)
	{
		int firsttime = 0;

		BigDecimal myNumber = new BigDecimal(n);
		BigDecimal g = new BigDecimal("1");
		BigDecimal my2 = new BigDecimal("2");
		BigDecimal epsilon = new BigDecimal("0.0000000001");

		BigDecimal nByg = myNumber.divide(g, 9, BigDecimal.ROUND_FLOOR);

		//Get the value of n/g
		BigDecimal nBygPlusg = nByg.add(g);

		//Get the value of "n/g + g
		BigDecimal nBygPlusgHalf =  nBygPlusg.divide(my2, 9, BigDecimal.ROUND_FLOOR);

		//Get the value of (n/g + g)/2
		BigDecimal  saveg = nBygPlusgHalf;
		firsttime = 99;

		do
		{
			g = nBygPlusgHalf;
			nByg = myNumber.divide(g, 9, BigDecimal.ROUND_FLOOR);
			nBygPlusg = nByg.add(g);
			nBygPlusgHalf =  nBygPlusg.divide(my2, 9, BigDecimal.ROUND_FLOOR);
			BigDecimal  savegdiff  = saveg.subtract(nBygPlusgHalf);

			if (savegdiff.compareTo(epsilon) == -1 )
			{
				firsttime = 0;
			}
			else
			{
				saveg = nBygPlusgHalf;
			}

		} while (firsttime > 1);
		return saveg.toBigInteger();
	}
}
