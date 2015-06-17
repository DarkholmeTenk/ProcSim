package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;

public class BranchLink extends Branch
{
	private int oldPC;
	public BranchLink(Conditional c, int id, String address)
	{
		super(c, id, address);
		oldPC = (id << 2) + 4;
	}

	public static IInstruction create(Conditional c, int id, String string)
	{
		return new BranchLink(c, id, string);
	}

	@Override
	public String getOutputRegister()
	{
		return "LR";
	}

	@Override
	public int getOutputRegisterValue()
	{
		return oldPC;
	}

	@Override
	public String toString()
	{
		return getID()+"BL" + c + " " + addrMnem;

	}
}
