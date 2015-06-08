package io.darkcraft.procsim.model.components.abstracts;

import io.darkcraft.procsim.model.components.registerbank.Register;
import io.darkcraft.procsim.model.instruction.IInstruction;

public interface IRegisterBank
{
	public boolean isRegister(String name);

	public Register get(String name);

	public Integer getStatus(IInstruction inst);

	public void unlockAll();

	public boolean hasForwarding(String name);

	/**Sets the value to be available before it's actually written back (i.e. forwarding)
	 * @param locker the instruction doing this
	 * @param name the name of the register
	 * @param value the value
	 */
	public void setAvailable(IInstruction locker, String name, int value);

	public void setProperly(IInstruction locker, String name, int value);

	/**Gets the current value stored in the register
	 */
	public Integer getValue(String name, IInstruction inst);

	void remove(IInstruction inst);

	public void incrementPC();
}
