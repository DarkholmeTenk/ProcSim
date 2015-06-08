package io.darkcraft.procsim.model.components.registerbank;

import io.darkcraft.procsim.model.error.RegisterAccessException;
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
		if (locker != null && !locker.equals(setter))
			throw new RegisterAccessException("Attempt to set by incorrect instruction");
		unlock();
		value = newValue;
	}

}
