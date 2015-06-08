package io.darkcraft.procsim.model.components;

import io.darkcraft.procsim.model.error.RegisterAccessException;
import io.darkcraft.procsim.model.instructions.IInstruction;

public class Register
{
	public final String	name;
	public int			value;
	public IInstruction	locker	= null;

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
		if(locker == null || locker.equals(_locker))
		{
			locker = _locker;
			return true;
		}
		return false;
	}

	public void set(IInstruction setter, int newValue)
	{
		if(!isLocked()) throw new RegisterAccessException("Attempt to set locked register");
		if(!locker.equals(setter)) throw new RegisterAccessException("Attempt to set by incorrect instruction");
		locker = null;
		value = newValue;
	}
}
