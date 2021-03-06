package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;

public abstract class AbstractOneLiteralOneOutInstruction extends AbstractInstruction
{
	private final String[]		inR;
	private final String		out;
	protected Integer[]			vals;
	protected int				outVal;

	public AbstractOneLiteralOneOutInstruction(Conditional c, int id, String _out, String in1)
	{
		super(c,id);
		out = _out;
		Integer a = ReadingHelper.literal(in1);
		if (a == null)
			inR = new String[] { in1 };
		else
			inR = new String[] { null };
		vals = new Integer[] { a };
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
		if(inR[i] != null)
			s += inR[i];
		else
			s += "#"+vals[i];
		return s;
	}

	@Override
	public String toString()
	{
		String soFar = getID() + getName() + c.toString();
		soFar += " " + out;
		soFar += toString(0);
		return soFar;
	}
}
