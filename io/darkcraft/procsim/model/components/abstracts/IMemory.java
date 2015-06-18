package io.darkcraft.procsim.model.components.abstracts;

import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.io.File;

/**
 * An interface to represent the connections to memory
 * @author Shane Booth
 *
 */
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
	 * sets the value in location and adds the requesting object to the list of memory operations to be processed
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

	/**
	 * Read all the memory information from the file
	 */
	public void read();

	/**
	 * Sets the file which represents the memory to newFile
	 * @param newFile the new file which represents the memory
	 */
	public void setFile(File newFile);

	/**
	 * Return a new object which represents the same thing as the current memory.
	 * If this is a cache object, the next level up should be cloned as well.
	 * @return
	 */
	public IMemory clone();

	/**
	 * @return an array of all of the memory which is connected to this memory (and this memory).
	 */
	public IMemory[] getStack();
}
