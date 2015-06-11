package io.darkcraft.procsim.model.components.registerbank;

import io.darkcraft.procsim.model.instruction.IInstruction;


public class PCReg extends Register
{

	public PCReg(int initial)
	{
		super("PC", initial);
	}

	public void increment()
	{
		value += 4;
	}

	@Override
	public boolean isLocked()
	{
		return false;
	}

	@Override
	public void set(IInstruction setter, int newValue)
	{
		unlock();
		value = newValue;
	}

}
