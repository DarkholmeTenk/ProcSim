package io.darkcraft.procsim.model.components.registerbank;



public class CPSRReg extends Register
{
	public CPSRReg(int initial)
	{
		super("CPSR", initial);
	}

	/*@Override
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
	}*/

}
