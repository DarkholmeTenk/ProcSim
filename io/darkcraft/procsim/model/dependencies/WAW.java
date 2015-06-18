package io.darkcraft.procsim.model.dependencies;

import io.darkcraft.procsim.controller.DependencyType;
import io.darkcraft.procsim.model.instruction.IInstruction;

/**
 * A Write After Write dependency.
 * @author mbax2sb4
 *
 */
public class WAW extends AbstractDependency
{
	public WAW(IInstruction _from, IInstruction _to)
	{
		super(_from,_to);
	}

	@Override
	public DependencyType getType()
	{
		return DependencyType.WAW;
	}


	@Override
	public String getDependentRegister()
	{
		return from.getOutputRegister();
	}
}
