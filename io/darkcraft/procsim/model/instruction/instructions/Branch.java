package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.InstructionReader;

public class Branch extends AbstractInstruction
{
	protected String		addrMnem	= null;
	protected String[]	register	= null;
	private int			addr;
	private int			offset		= 0;

	public Branch(Conditional c, int id, String address)
	{
		super(c, id);
		addrMnem = address;
		register = null;
	}

	public Branch(Conditional c, int id, String _reg, String _off)
	{
		super(c, id);
		Integer off = ReadingHelper.literal(_off);
		if (_off == null)
			off = 0;
		if (off == null)
			register = new String[]{ _reg, _off };
		else
		{
			offset = off;
			register = new String[]{ _reg };
		}
	}

	@Override
	public String[] getInputRegisters()
	{
		return register;
	}

	@Override
	public void setInputRegisters(int[] values)
	{
		if(addrMnem == null)
		{
			addr = values[0];
			if(values.length > 1)
				offset = values[2];
		}
	}

	public int getAddress(InstructionReader reader)
	{
		if(addrMnem == null)
			return addr + offset;
		return reader.get(addrMnem) << 2;
	}

	@Override
	public String getOutputRegister()
	{
		return null;
	}

	@Override
	public int getOutputRegisterValue()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return getID()+"B" + c + " " + addrMnem;

	}

	@Override
	public String getName()
	{
		return "B";
	}

}
