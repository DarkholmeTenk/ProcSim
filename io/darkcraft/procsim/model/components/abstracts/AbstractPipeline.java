package io.darkcraft.procsim.model.components.abstracts;

import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.util.List;

/**
 * An abstract class representing a pipeline.
 * @author Shane Booth
 *
 */
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

	/**
	 * Removes any instruction whose start time occurs After spot
	 * @param spot the time to remove everything after.
	 */
	public abstract void clearAfter(int spot);

	/**
	 * @param inst the instruction to get the position of
	 * @return the stage which the instruction is in or null if the instruction does not exist at any stage.
	 */
	public abstract Integer getPosition(IInstruction inst);

	/**
	 * @return an array representative of the current state of the pipeline
	 */
	public abstract IInstruction[] getState();

	public abstract List<String> getDangerousOut();

	public abstract List<String> getDangerousIn();

	/**
	 * @param stage the stage to check
	 * @return the getTimeStarted() of the instruction currently in stage or -1 if there is no instruction
	 */
	public abstract int getInstructionTime(int stage);

	/**
	 * @param stage the stage to check
	 * @return the instruction currently in the stage or null if there is nothing in that stage.
	 */
	public abstract IInstruction getInstruction(int stage);

	/**
	 * Progresses a stage forwards slightly
	 * @param sim the simulator which caused the step
	 * @param stage the stage to progress
	 */
	public abstract void stepStage(AbstractSimulator sim, int stage);

	/**
	 * Increments a timer which is used to ensure that all dispatched instructions have an appropriate lamport time
	 */
	public abstract void increaseTimer();

	/**
	 * @return the last stage which can be identified as ID.
	 */
	public abstract int getLastIDStage();

	/**
	 * @param stage the stage
	 * @return true if stage is the first stage in a functional unit.
	 */
	public abstract boolean isFirstExeStage(int stage);

	/**
	 * Returns a 2D representation of all the functional units.
	 * Used to ensure consistent instruction execution
	 * @return A 2D array of (functional unit, stage inside)
	 */
	public abstract int[][] getExeBlocks();
}
