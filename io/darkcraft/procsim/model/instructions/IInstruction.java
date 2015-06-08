package io.darkcraft.procsim.model.instructions;

public interface IInstruction
{
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
	 * Run the execute phase of the instruction
	 */
	public void doExecute();

	/**
	 * Run the memory phase of the instruction
	 */
	public void doMemory();

	/**
	 * @return the identifier of the output register, or null if no output (e.g. STR)
	 */
	public String getOutputRegister();

	/**
	 * @return the value the output register should be set to
	 */
	public int getOutputRegisterValue();
}
