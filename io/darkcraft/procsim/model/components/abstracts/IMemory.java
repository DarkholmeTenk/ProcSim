package io.darkcraft.procsim.model.components.abstracts;

import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.io.File;

public interface IMemory
{
	/**
	 * @return the size of the memory in words
	 */
	public int getSize();

	/**
	 * @return the size of a word in ints
	 */
	public int getWordSize();

	/**
	 * gets the value from location
	 */
	public int getValue(Object i, int location);

	/**
	 * sets the value in location
	 */
	public void setValue(Object i, int location, int value);

	/**
	 * gets an address from an identifier
	 */
	public int getLocation(String identifier);

	/**
	 * Checks whether an instruction has elapsed enough cycles for its memory request to finish
	 * @param i the instruction to check
	 * @return true if the memory instruction has finished
	 * @return true if the instruction never interacted with the memory
	 */
	public boolean doneOperation(Object i);

	/**
	 * Increments the internal time in the memory for the purpose of making operations take time
	 */
	public void step(AbstractSimulator sim);

	public void read();

	public void setFile(File newFile);

	public IMemory clone();

	public IMemory[] getStack();
}
