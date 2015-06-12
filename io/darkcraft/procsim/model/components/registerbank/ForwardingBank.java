package io.darkcraft.procsim.model.components.registerbank;

public class ForwardingBank extends StandardBank
{
	public ForwardingBank(int _size)
	{
		super(_size);
	}

	@Override
	public boolean hasForwarding(String name)
	{
		if(name == null) return false;
		if(name.equals("PC") || name.equals("CPSR") || name.startsWith("R")) return true;
		return false;
	}
}
