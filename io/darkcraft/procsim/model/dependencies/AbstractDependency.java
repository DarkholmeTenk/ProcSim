package io.darkcraft.procsim.model.dependencies;

import io.darkcraft.procsim.model.instruction.IInstruction;

/**
 * A class to provide some functionality universal to all of the dependencies I implemented
 * @author Shane Booth
 *
 */
public abstract class AbstractDependency implements IDependency
{
	public final IInstruction from;
	public final IInstruction to;

	public AbstractDependency(IInstruction _from, IInstruction _to)
	{
		from = _from;
		to = _to;
	}

	@Override
	public IInstruction getFrom()
	{
		return from;
	}

	@Override
	public IInstruction getTo()
	{
		return to;
	}

	@Override
	public String toString()
	{
		return getType() + " " + from.toString() + "->"+to.toString();
	}
}