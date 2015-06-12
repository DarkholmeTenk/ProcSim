package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;

public class BranchLink extends Branch
{
	private static final String[] pcin = new String[]{"PC"};
	public BranchLink(Conditional c, int id, String address)
	{
		super(c, id, address);
		register = pcin;
	}

	public BranchLink(Conditional c, int id, String _reg, String _off)
	{
		super(c, id, _reg, _off);
		Integer off = ReadingHelper.literal(_off);
		if (_off == null)
			off = 0;
		if (off == null)
			register = new String[]{ "PC", _reg, _off };
		else
			register = new String[]{ "PC", _reg };
	}
	//TODO: loopback
}
