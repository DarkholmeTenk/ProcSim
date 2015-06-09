package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.helper.Pair;
import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IMemoryInstruction;
import io.darkcraft.procsim.model.instruction.MemoryInstructionType;

public class Store extends AbstractInstruction implements IMemoryInstruction
{

	String						address;
	Integer						adr			= null;
	int							val;

	String[]					register	= null;
	int							offset;

	public static Store create(Conditional c, int id, String data)
	{
		String[] split = data.split(ReadingHelper.splitRegex,2);
		Pair<String,String> registers = ReadingHelper.getAddressingRegister(split[1]);
		if(registers != null)
			return new Store(c, id, split[0], registers.a, registers.b);
		return new Store(c, id, split[0], split[1]);
	}

	public Store(Conditional c, int id, String _out, String mem)
	{
		super(c, id);
		register = new String[]{_out};
		address = mem;
		offset = 0;
	}

	public Store(Conditional c, int id, String _out, String _reg, String _off)
	{
		super(c, id);
		Integer off = ReadingHelper.literal(_off);
		if (_off == null)
			off = 0;
		if (off == null)
			register = new String[]
			{ _out, _reg, _off };
		else
		{
			offset = off;
			register = new String[]
			{ _out, _reg };
		}
	}

	@Override
	public String[] getInputRegisters()
	{
		if (register != null)
			return register;
		return new String[] {};
	}

	@Override
	public void setInputRegisters(int[] values)
	{
		if (register != null)
		{
			if (register.length == 3)
				offset = values[2];
			if (register.length >= 2)
				adr = values[1];
			val = values[0];
		}
	}

	@Override
	public void doMemory(IMemory mem)
	{
		if (adr == null)
			adr = mem.getLocation(address);
		mem.setValue(this, adr+offset, val);
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
		return getID()+"STR"+c.getName() + " " +register[0] + " " + (address != null ? address :
			(register[1] + (register.length == 3 ? " " +register[2] :" #" + offset)));
	}

	@Override
	public MemoryInstructionType getType()
	{
		return MemoryInstructionType.WRITE;
	}

	@Override
	public String getName()
	{
		return "STR";
	}
}