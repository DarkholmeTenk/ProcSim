package io.darkcraft.procsim.model.helper;

public class Pair<T1, T2>
{
	public final T1 a;
	public final T2 b;
	public Pair(T1 _a, T2 _b)
	{
		a = _a;
		b = _b;
	}

	@Override
	public String toString()
	{
		return "<" + a.toString() + "," + b.toString() + ">";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pair))
			return false;
		Pair other = (Pair) obj;
		if (a == null)
		{
			if (other.a != null)
				return false;
		}
		else if (!a.equals(other.a))
			return false;
		if (b == null)
		{
			if (other.b != null)
				return false;
		}
		else if (!b.equals(other.b))
			return false;
		return true;
	}
}
