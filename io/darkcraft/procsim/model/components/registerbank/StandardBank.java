package io.darkcraft.procsim.model.components.registerbank;

import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.instruction.IInstruction;

import java.util.HashMap;

public class StandardBank implements IRegisterBank
{
	private final int			size;
	private final Register[]	registers;

	public StandardBank(int _size)
	{
		size = _size;
		registers = new Register[size];
		for(int i = 0; i < size -4; i++)
			registers[i] = new Register("R"+i,0);
		registers[size-4] = new Register("SP",0);
		registers[size-3] = new Register("LR",0);
		registers[size-2] = new PCReg(0);
		registers[size-1] = new CPSRReg(0);
	}

	@Override
	public boolean isRegister(String name)
	{
		return getSlot(name) != -1;
	}

	private int getSlot(String name)
	{
		if(name == null)
			return -1;
		if (name.startsWith("R"))
		{
			String num = name.substring(1);
			try
			{
				Integer i = Integer.parseInt(num);
				if (i < size - 4 && i >= 0)
					return i;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return -1;
		}
		if (name.equalsIgnoreCase("SP"))
			return size - 4;
		if (name.equalsIgnoreCase("LR"))
			return size - 3;
		if (name.equalsIgnoreCase("PC"))
			return size - 2;
		if (name.equalsIgnoreCase("CPSR"))
			return size - 1;
		return -1;
	}

	private HashMap<String, Integer> forwarded = new HashMap<String, Integer>();
	@Override
	public void setAvailable(IInstruction locker, String name, int value)
	{
		if(hasForwarding(name))
			forwarded.put(name, value);
	}

	@Override
	public void remove(IInstruction inst)
	{
		String s = inst.getOutputRegister();
		if(s != null)
		{
			Register r = get(s);
			if(r.getLockers().contains(inst))
				r.unlock(inst);
		}
	}

	@Override
	public void setProperly(IInstruction locker, String name, int value)
	{
		Integer forwardedVal = null;
		if(forwarded.containsKey(name))
			forwardedVal = forwarded.remove(name);
		Register r = get(name);
		if(name.equals("PC"))
			r.set(locker, forwardedVal);
		else
			r.set(locker, value);
	}

	@Override
	public Integer getValue(String name,IInstruction inst)
	{
		if(forwarded.containsKey(name))
			return forwarded.get(name);
		Register r = get(name);
		if(r.isLocked() && !(r.getLockers().size()== 1 && r.getLockers().contains(inst)))
			return null;
		return r.getValue();
	}

	@Override
	public Register get(String name)
	{
		int slot = getSlot(name);
		if(slot != -1)
			return registers[slot];
		return null;
	}

	@Override
	public boolean hasForwarding(String name)
	{
		if(name == null) return false;
		if(name.equals("PC") || name.equals("CPSR")) return true;
		return false;
	}

	@Override
	public void unlockAll()
	{
		for(Register i : registers)
			i.unlock();
	}

	@Override
	public Integer getStatus(IInstruction inst)
	{
		return getValue("CPSR",inst);
	}

	@Override
	public void incrementPC()
	{
		if(forwarded.containsKey("PC"))
			forwarded.put("PC",forwarded.get("PC")+4);
		PCReg r = (PCReg) get("PC");
		r.increment();
	}
}
