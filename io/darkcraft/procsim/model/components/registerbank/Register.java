package io.darkcraft.procsim.model.components.registerbank;

import io.darkcraft.procsim.model.instruction.IInstruction;

public class Register
{
	public final String		name;
	protected int			value;
	protected IInstruction	locker	= null;
	protected IInstruction 	lastLocker = null;

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
		locker = _locker;
		return true;
	}

	public void unlock()
	{
		lastLocker = locker;
		locker = null;
	}

	public void set(IInstruction setter, int newValue)
	{
		//if((locker != null && locker.equals(setter)) || (lastLocker != null && locker == null && setter == lastLocker))
		{
			unlock();
			value = newValue;
		}
	}

	public int getValue()
	{
		return value;
	}

	public IInstruction getLocker()
	{
		return locker;
	}

	@Override
	public String toString()
	{
		return name + " - " + value;
	}
}
