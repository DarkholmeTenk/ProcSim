package io.darkcraft.procsim.model.dependencies;

import io.darkcraft.procsim.controller.DependencyType;
import io.darkcraft.procsim.model.instruction.IInstruction;

/**
 * An interface for interacting with dependencies as a unified type
 * @author Shane Booth
 *
 */
public interface IDependency
{
	/**
	 * @return The instruction the dependency is from (the beginning of the arrow)
	 */
	public IInstruction getFrom();

	/**
	 * @return The instruction the dependency is to (the end of the arrow)
	 */
	public IInstruction getTo();

	/**
	 * @return The register which is causing the dependency
	 */
	public String getDependentRegister();

	/**
	 * @return the type of dependency this is
	 */
	public DependencyType getType();
}
