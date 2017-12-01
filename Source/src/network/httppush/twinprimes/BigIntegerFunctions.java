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
package network.httppush.twinprimes;

import java.math.BigInteger;

import ca.uqac.lif.cep.functions.BinaryFunction;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.UnaryFunction;

/**
 * Contains a few utility functions for manipulating Java's {@link BigInteger}
 * objects.
 * @author Sylvain Hallé
 */
public class BigIntegerFunctions
{
	/**
	 * Converts a BigInteger into a string
	 */
	public static class BigIntegerToString extends UnaryFunction<BigInteger,String>
	{
		public static final transient BigIntegerToString instance = new BigIntegerToString();

		BigIntegerToString()
		{
			super(BigInteger.class, String.class);
		}

		@Override
		public String getValue(BigInteger x) throws FunctionException 
		{
			return x.toString();
		}
	}
	
	/**
	 * Adds two BigIntegers
	 */
	public static class BigIntegerAdd extends BinaryFunction<BigInteger,BigInteger,BigInteger>
	{
		public static final transient BigIntegerAdd instance = new BigIntegerAdd();
		
		BigIntegerAdd()
		{
			super(BigInteger.class, BigInteger.class, BigInteger.class);
		}

		@Override
		public BigInteger getValue(BigInteger x, BigInteger y)
		{
			return x.add(y);
		}
		
		@Override
		public BigInteger getStartValue()
		{
			return BigInteger.ONE;
		}
	}

	/**
	 * Converts a string into a BigInteger
	 */
	public static class StringToBigInteger extends UnaryFunction<String,BigInteger>
	{
		public static final transient StringToBigInteger instance = new StringToBigInteger();
		
		StringToBigInteger()
		{
			super(String.class, BigInteger.class);
		}

		@Override
		public BigInteger getValue(String x) throws FunctionException 
		{
			return new BigInteger(x);
		}
	}

	/**
	 * Checks if a BigInteger is prime. This function uses a very slow method.
	 */
	public static class IsPrime extends UnaryFunction<BigInteger,Boolean>
	{
		public static final transient IsPrime instance = new IsPrime();
		private static final transient BigInteger TWO = new BigInteger("2");
		private static final transient BigInteger THREE = new BigInteger("3");

		IsPrime()
		{
			super(BigInteger.class, Boolean.class);
		}

		@Override
		public Boolean getValue(BigInteger n) throws FunctionException 
		{
			/* This algorithm was found on
			 * https://codereview.stackexchange.com/q/43490 */
			if (n.compareTo(BigInteger.ONE) == 0 || n.compareTo(TWO) == 0)
			{
				return true;
			}
			BigInteger half=n.divide(TWO);
			for (BigInteger i = THREE; i.compareTo(half) <= 0; i = i.add(TWO))
			{

				if (n.mod(i).equals(BigInteger.ZERO))
				{
					return false; 
				}
			}
			return true;
		}
	}
	
	/**
	 * Increments a BigInteger by a specific value 
	 */
	public static class IncrementBigInteger extends UnaryFunction<BigInteger,BigInteger>
	{
		/**
		 * The increment
		 */
		protected final BigInteger m_increment;
		
		/**
		 * Creates an instance of the BigInteger increment function
		 * @param increment The increment
		 */
		public IncrementBigInteger(BigInteger increment)
		{
			super(BigInteger.class, BigInteger.class);
			m_increment = increment;
		}

		@Override
		public BigInteger getValue(BigInteger x) throws FunctionException 
		{
			return m_increment.add(x);
		}
	}
	
	
}