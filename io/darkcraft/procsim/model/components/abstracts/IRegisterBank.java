package io.darkcraft.procsim.model.components.abstracts;

import io.darkcraft.procsim.model.components.registerbank.Register;
import io.darkcraft.procsim.model.instruction.IInstruction;

/**
 * An interface for representing the register bank.
 * Also represents the forwarding circuitry in the pipeline, because that simplifies logic slightly.
 * @author Shane Booth
 *
 */
public interface IRegisterBank
{
	/**
	 * @param name
	 * @return true if name is a valid register within this register bank and false otherwise
	 */
	public boolean isRegister(String name);

	/**
	 * @param name
	 * @return the register represented by name
	 */
	public Register get(String name);

	/**
	 * @param inst the instruction which is trying to access the value of the status register
	 * @return the current value of the status register or null if it is unavailable
	 */
	public Integer getStatus(IInstruction inst);

	/**
	 * Removes all locks on all registers
	 */
	public void unlockAll();

	/**
	 * @param name
	 * @return true if the register identified by name should have forwarding enabled.
	 */
	public boolean hasForwarding(String name);

	/**
	 * Sets the value to be available before it's actually written back (i.e. for forwarding)
	 * @param locker the instruction doing this
	 * @param name the name of the register
	 * @param value the value
	 */
	public void setAvailable(IInstruction locker, String name, int value);

	/**
	 * Properly set the new value to be stored in the register
	 * @param locker The instruction attempting to do the setting
	 * @param name The name of the register whose value is being set
	 * @param value The new value of the register
	 */
	public void setProperly(IInstruction locker, String name, int value);

	/**
	 * Gets the current value stored in the register
	 * @param name the name of the register to get the value of
	 * @param inst the instruction which is attempting to get the value of the register.
	 * @return the current value of the register or null if inst cannot access the value yet
	 */
	public Integer getValue(String name, IInstruction inst);

	/**
	 * Remove all locks caused by instruction inst
	 * @param inst the instruction to remove
	 */
	void remove(IInstruction inst);

	/**
	 * Increments the PC register by 4.
	 */
	public void incrementPC();
}
