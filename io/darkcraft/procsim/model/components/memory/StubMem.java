package io.darkcraft.procsim.model.components.memory;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.instruction.IInstruction;

public class StubMem implements IMemory
{

	@Override
	public int getSize()
	{
		return 0;
	}

	@Override
	public int getWordSize()
	{
		return 0;
	}

	@Override
	public int getValue(IInstruction i, int location)
	{
		return 0;
	}

	@Override
	public void setValue(IInstruction i, int location, int value)
	{
	}

	@Override
	public int getLocation(String identifier)
	{
		return 0;
	}

	@Override
	public boolean doneOperation(IInstruction i)
	{
		return true;
	}

}
