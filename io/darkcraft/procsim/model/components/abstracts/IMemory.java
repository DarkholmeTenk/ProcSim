package io.darkcraft.procsim.model.components.abstracts;

import io.darkcraft.procsim.model.instruction.IInstruction;

public interface IMemory
{
	/**
	 * @return the size of the memory in words
	 */
	public int getSize();

	/**
	 * @return the size of a word in bytes
	 */
	public int getWordSize();

	/**
	 * gets the value from location
	 */
	public int getValue(IInstruction i, int location);

	/**
	 * sets the value in location
	 */
	public void setValue(IInstruction i, int location, int value);

	/**
	 * gets an address from an identifier
	 */
	public int getLocation(String identifier);

	public boolean doneOperation(IInstruction i);
}
