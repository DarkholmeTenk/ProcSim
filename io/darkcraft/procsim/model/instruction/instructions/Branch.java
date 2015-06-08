package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

public class Branch implements IInstruction
{
	private Conditional	c;
	private String		addrMnem	= null;
	private String[]	register	= null;
	private int			addr;
	private int			offset		= 0;

	public Branch(Conditional _c, String address)
	{
		addrMnem = address;
		c = _c;
		register = null;
	}

	public Branch(Conditional _c, String _reg, String _off)
	{
		c = _c;
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
	public Conditional getConditional()
	{
		return c;
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

	@Override
	public void doExecute()
	{

	}

	public int getAddress(InstructionReader reader)
	{
		if(addrMnem == null)
			return addr + offset;
		return reader.get(addrMnem) << 2;
	}

	@Override
	public void doMemory(IMemory mem)
	{
	}

	@Override
	public String getOutputRegister()
	{
		return "PC";
	}

	@Override
	public int getOutputRegisterValue()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "B" + c + " " + addrMnem;

	}

}
