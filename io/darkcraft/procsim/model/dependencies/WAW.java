package io.darkcraft.procsim.model.dependencies;

import io.darkcraft.procsim.model.instruction.IInstruction;

public class WAW implements IDependency
{
	public final IInstruction from;
	public final IInstruction to;

	public WAW(IInstruction _from, IInstruction _to)
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
		return "WAW";
	}

	@Override
	public String toString()
	{
		return getType() + " " + from.toString() + "->"+to.toString();
	}
}
