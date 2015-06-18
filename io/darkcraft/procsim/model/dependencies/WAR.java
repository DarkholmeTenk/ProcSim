package io.darkcraft.procsim.model.dependencies;

import io.darkcraft.procsim.controller.DependencyType;
import io.darkcraft.procsim.model.instruction.IInstruction;

/**
 * A Write After Read dependency
 * @author Shane Booth
 *
 */
public class WAR extends AbstractDependency
{

	public WAR(IInstruction _from, IInstruction _to)
	{
		super(_from,_to);
	}

	@Override
	public DependencyType getType()
	{
		return DependencyType.WAR;
	}

	@Override
	public String getDependentRegister()
	{
		return to.getOutputRegister();
	}
}