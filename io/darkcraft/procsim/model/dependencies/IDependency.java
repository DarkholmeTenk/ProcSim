package io.darkcraft.procsim.model.dependencies;

import io.darkcraft.procsim.model.instruction.IInstruction;

public interface IDependency
{
	public IInstruction getFrom();

	public IInstruction getTo();

	public String getType();
}
