package io.darkcraft.procsim.model.components.registerbank;

import io.darkcraft.procsim.model.error.RegisterAccessException;
import io.darkcraft.procsim.model.instruction.IInstruction;

public class Register
{
	public final String		name;
	protected int			value;
	protected IInstruction	locker	= null;

	public Register(String _name, int initial)
	{
		name = _name;
		value = initial;
	}

	public boolean isLocked()
	{
		return locker != null;
	}

	public boolean lock(IInstruction _locker)
	{
		if (locker == null || locker.equals(_locker))
		{
			locker = _locker;
			return true;
		}
		return false;
	}

	public void unlock()
	{
		locker = null;
	}

	public void set(IInstruction setter, int newValue)
	{
		if (!isLocked())
			throw new RegisterAccessException("Attempt to set locked register");
		if (!locker.equals(setter))
			throw new RegisterAccessException("Attempt to set by incorrect instruction");
		unlock();
		value = newValue;
	}

	public int getValue()
	{
		return value;
	}

	public IInstruction getLocker()
	{
		return locker;
	}
}
