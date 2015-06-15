package io.darkcraft.procsim.model.dependencies;

import io.darkcraft.procsim.controller.DependencyType;
import io.darkcraft.procsim.model.instruction.IInstruction;

public class RAW extends AbstractDependency
{
	public RAW(IInstruction _from, IInstruction _to)
	{
		super(_from,_to);
	}

	@Override
	public DependencyType getType()
	{
		return DependencyType.RAW;
	}

	@Override
	public String getDependentRegister()
	{
		return from.getOutputRegister();
	}
}
