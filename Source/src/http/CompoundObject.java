package http;
/**
 * A dummy object used to test serialization
 */
public class CompoundObject
{
	int a;
	String b;
	CompoundObject c;
	
	protected CompoundObject()
	{
		super();
	}
	
	public CompoundObject(int a, String b, CompoundObject c)
	{
		super();
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof CompoundObject))
		{
			return false;
		}
		CompoundObject co = (CompoundObject) o;
		if (this.a != co.a || this.b.compareTo(co.b) != 0)
		{
			return false;
		}
		if ((this.c == null && co.c == null) || (this.c != null && co.c != null && this.c.equals(co.c)))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return "a = " + a + ", b = " + b + ", c = (" + c + ")";
	}
}