package io.darkcraft.procsim.model.instruction;

import io.darkcraft.procsim.model.components.abstracts.IMemory;

public interface IInstruction
{
	public Conditional getConditional();

	/**
	 * @return an array containing the identifiers of all the registers to fetch during ID
	 */
	public String[] getInputRegisters();

	/**
	 * Passes all of the values from the input registers to the instruction
	 * @param values the values of the input registers in the order given above
	 */
	public void setInputRegisters(int[] values);

	/**
	 * Called by the pipeline if the conditional failed
	 */
	public void conditionalFailed();

	/**
	 * Run the execute phase of the instruction
	 */
	public void doExecute();

	/**
	 * Run the memory phase of the instruction.
	 * Should register a request to modify the memory. Pipeline simulator should stall as long as necessary.
	 */
	public void doMemory(IMemory mem);

	/**
	 * @return the identifier of the output register, or null if no output (e.g. STR)
	 */
	public String getOutputRegister();

	/**
	 * @return the value the output register should be set to
	 */
	public int getOutputRegisterValue();

	/**
	 * @return true if the doExecute method has been run
	 */
	public boolean hasExecuted();

	/**
	 * @return true if the conditional failed
	 */
	public boolean didFail();

	/**
	 * @return true if the instruction has entered the pipeline
	 */
	public boolean hasStarted();

	/**
	 * Should be run by the simulator when the instruction first enters the pipeline
	 */
	public void start();
}
