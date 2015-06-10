package io.darkcraft.procsim.model.components.memory;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

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
	public int getValue(Object i, int location)
	{
		return 0;
	}

	@Override
	public void setValue(Object i, int location, int value)
	{
	}

	@Override
	public int getLocation(String identifier)
	{
		return 0;
	}

	@Override
	public boolean doneOperation(Object i)
	{
		return true;
	}

	@Override
	public void step(AbstractSimulator sim)
	{
	}

	@Override
	public void read()
	{
		// TODO Auto-generated method stub

	}

}
