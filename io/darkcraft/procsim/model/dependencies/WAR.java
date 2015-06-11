package io.darkcraft.procsim.model.dependencies;

import io.darkcraft.procsim.model.instruction.IInstruction;

public class WAR implements IDependency
{
	public final IInstruction from;
	public final IInstruction to;

	public WAR(IInstruction _from, IInstruction _to)
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
	public String getType()
	{
		return "WAR";
	}

	@Override
	public String toString()
	{
		return getType() + " " + from.toString() + "->"+to.toString();
	}
}