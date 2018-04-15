package concurrency;

import java.math.BigInteger;

import ca.uqac.lif.cep.functions.BinaryFunction;

public class BigIntegers
{
	public static final BigIntegerAddition addition = new BigIntegerAddition();
	
	public static class BigIntegerAddition extends BinaryFunction<BigInteger,BigInteger,BigInteger>
	{
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
}
