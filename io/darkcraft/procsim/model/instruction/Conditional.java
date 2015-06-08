package io.darkcraft.procsim.model.instruction;

public enum Conditional
{
	AL(null,	null,	false,	""),
	GT(false,	false,	false,	"GT"),
	GE(false,	true,	true,	"GE"),
	LT(true,	false,	false,	"LT"),
	LE(true,	true,	true,	"LE"),
	EQ(null,	true, 	false,	"EQ"),
	NE(null,	false,	false,	"NE");

	public static final int		negMask	= 0x8000;
	public static final int		zerMask	= 0x4000;
	private final Boolean		neg;
	private final Boolean		zer;
	private final boolean		e;
	private final String		name;

	/**
	 * @param negative
	 *            whether the negative flag needs to be set
	 * @param zero
	 *            whether the zero flag needs to be set
	 * @param either
	 *            if true, only 1 of the 2 flags needs to be set
	 */
	private Conditional(Boolean negative, Boolean zero, boolean either, String _name)
	{
		neg = negative;
		zer = zero;
		e = either;
		name = _name;
	}

	public String getName()
	{
		return name;
	}

	public boolean match(int data)
	{
		boolean negative = (data & negMask) == negMask;
		boolean zero = (data & zerMask) == zerMask;
		boolean negSat = (neg == null) || (neg == negative);
		boolean zerSat = (zer == null) || (zer == zero);
		if(e) return negSat || zerSat;
		return negSat && zerSat;
	}

	public static Conditional get(String instruction)
	{
		for(Conditional c : values())
			if(instruction.endsWith(c.name()))
				return c;
		return AL;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
