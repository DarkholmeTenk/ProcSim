package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.helper.Pair;
import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.IMemoryInstruction;
import io.darkcraft.procsim.model.instruction.MemoryInstructionType;

public class Load extends AbstractInstruction implements IMemoryInstruction
{
	public static IInstruction create(Conditional c, int id, String data)
	{
		String[] split = data.split(ReadingHelper.splitRegex,2);
		Pair<String,String> regOff = ReadingHelper.getAddressingRegister(split[1]);
		if(regOff == null)
			return new Load(c,id,split[0],split[1]);
		else
			return new Load(c,id,split[0],regOff.a,regOff.b);
	}

	String						address;
	private final String		out;
	Integer						adr			= null;
	int							val;

	String[]					register	= null;
	int							offset;

	public Load(Conditional c, int id, String _out)
	{
		super(c, id);
		out = _out;
	}

	public Load(Conditional c, int id, String _out, String mem)
	{
		this(c, id, _out);
		address = mem;
		offset = 0;
	}

	public Load(Conditional c, int id, String _out, String _reg, String _off)
	{
		this(c, id, _out);
		Integer off = ReadingHelper.literal(_off);
		if (_off == null)
			off = 0;
		if (off == null)
			register = new String[]
			{ _reg, _off };
		else
		{
			offset = off;
			register = new String[]
			{ _reg };
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
			if (register.length == 2)
				offset = values[1];
			adr = values[0];
		}
	}

	@Override
	public void doMemory(IMemory mem)
	{
		if (adr == null)
			adr = mem.getLocation(address);
		val = mem.getValue(this, adr+offset);
	}

	@Override
	public String getOutputRegister()
	{
		return out;
	}

	@Override
	public int getOutputRegisterValue()
	{
		return val;
	}

	@Override
	public String toString()
	{
		return getID() + "LDR"+c.getName() + " " + out + " " +  (address != null ? address :
			(register[0] + (register.length == 2 ? " " +register[2] :" #" + offset)));
	}

	@Override
	public MemoryInstructionType getType()
	{
		return MemoryInstructionType.READ;
	}

	@Override
	public String getName()
	{
		return "LDR";
	}
}
