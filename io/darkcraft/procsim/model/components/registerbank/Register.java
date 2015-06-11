package io.darkcraft.procsim.model.components.registerbank;

import io.darkcraft.procsim.model.instruction.IInstruction;

import java.util.ArrayList;
import java.util.List;

public class Register
{
	public final String		name;
	protected int			value;
	protected List<IInstruction>	lockers	= new ArrayList<IInstruction>();

	public Register(String _name, int initial)
	{
		name = _name;
		value = initial;
	}

	public boolean isLocked()
	{
		return lockers.size() > 0;
	}

	public boolean lock(IInstruction _locker)
	{
		if(!lockers.contains(_locker))
		lockers.add(_locker);
		return true;
	}

	public void unlock()
	{
		lockers.clear();
	}

	public void unlock(IInstruction i)
	{
		lockers.remove(i);
	}

	public void set(IInstruction setter, int newValue)
	{
		unlock(setter);
		value = newValue;
	}

	public int getValue()
	{
		return value;
	}

	public List<IInstruction> getLockers()
	{
		return lockers;
	}

	@Override
	public String toString()
	{
		return name + " - " + value;
	}
}
