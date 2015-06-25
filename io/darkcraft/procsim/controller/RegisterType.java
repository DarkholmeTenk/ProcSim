package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.registerbank.ForwardingBank;
import io.darkcraft.procsim.model.components.registerbank.StandardBank;

public enum RegisterType
{
	STANDARD16("Standard - 16 Registers"),
	FORWARD16("Forwarding - 16 Registers");

	public final String name;
	private RegisterType(String _name)
	{
		name = _name;
	}

	public String getName()
	{
		return name;
	}

	public IRegisterBank construct()
	{
		switch(this)
		{
		case STANDARD16: return new StandardBank(16);
		case FORWARD16: return new ForwardingBank(16);
		default: return null;
		}
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public static RegisterType get(String name)
	{
		for(RegisterType p : values())
			if(p.getName().equals(name))
				return p;
		return null;
	}
}
