package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;

public abstract class AbstractTwoLiteralOneOutInstruction implements IInstruction
{
	protected final Conditional	c;
	protected final String[]		inR;
	protected final String		out;
	protected Integer[]			vals;
	protected int				outVal;

	public AbstractTwoLiteralOneOutInstruction(Conditional _c, String _out, String in1, String in2)
	{
		c = _c;
		out = _out;
		Integer a = ReadingHelper.literal(in1);
		Integer b = ReadingHelper.literal(in2);
		if (a == null && b == null)
			inR = new String[]
			{ in1, in2 };
		else if (b == null)
			inR = new String[]
			{ null, in2 };
		else if (a == null)
			inR = new String[]
			{ in1, null };
		else
			inR = new String[]
			{ null, null };
		vals = new Integer[]
		{ a, b };
	}

	@Override
	public Conditional getConditional()
	{
		return c;
	}

	@Override
	public String[] getInputRegisters()
	{
		return inR;
	}

	@Override
	public void setInputRegisters(int[] values)
	{
		for (int i = 0; i < values.length; i++)
		{
			if (inR[i] != null)
				vals[i] = values[i];
		}
	}

	@Override
	public String getOutputRegister()
	{
		return out;
	}

	@Override
	public int getOutputRegisterValue()
	{
		return outVal;
	}

	protected String toString(int i)
	{
		String s = " ";
		if(inR[i] != null) s += inR[i];
		if(vals[i] != null)
		{
			if(inR[i] != null)
				s+= "("+vals[i]+")";
			else
				s+= "#"+vals[i];
		}
		return s;
	}

	@Override
	public String toString()
	{
		String soFar = c.toString();
		soFar += " " + out;
		soFar += toString(0);
		soFar += toString(1);
		return soFar;
	}
}
