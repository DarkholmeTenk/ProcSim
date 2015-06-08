package io.darkcraft.procsim.model.components.abstracts;

import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

public abstract class AbstractPipeline
{
	protected final IMemory memory;
	protected final IRegisterBank registers;
	protected final InstructionReader reader;

	protected AbstractPipeline(IMemory _memory, IRegisterBank _registers, InstructionReader _reader)
	{
		memory = _memory;
		registers = _registers;
		reader = _reader;
	}

	/**
	 * @param in the instruction to add to the pipeline
	 * @return true if the instruction was successfully added
	 */
	public abstract boolean addInstruction(IInstruction in);

	/**
	 * Move all the instructions along one step
	 */
	public abstract void step();

	/**
	 * @return an array containing the full name of all the pipeline stages
	 */
	public abstract String[] getPipelineStages();

	/**
	 * @return an array containing the short name of all the pipeline stages
	 */
	public abstract String[] getPipelineShorts();

	/**
	 * @return true if there are no instructions still executing
	 */
	public abstract boolean isEmpty();

	/**
	 * Flushes the pipeline for after a pc change
	 */
	public abstract void clear();

	public abstract void clearUpTo(int spot);

	public abstract Integer getPosition(IInstruction inst);

	public abstract IInstruction[] getState();
}
