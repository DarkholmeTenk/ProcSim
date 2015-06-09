package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;

public abstract class AbstractInstruction implements IInstruction
{
	public final Conditional	c;
	private boolean				started		= false;
	private boolean				executed	= false;
	private boolean				failed		= false;

	public AbstractInstruction(Conditional _c)
	{
		c = _c;
	}

	@Override
	public Conditional getConditional()
	{
		return c;
	}

	@Override
	public void start()
	{
		started = true;
	}

	@Override
	public boolean hasStarted()
	{
		return started;
	}

	@Override
	public void doExecute()
	{
		executed = true;
	}

	@Override
	public void doMemory(IMemory mem)
	{

	}

	@Override
	public boolean hasExecuted()
	{
		return executed;
	}

	@Override
	public void conditionalFailed()
	{
		failed = true;
	}

	@Override
	public boolean didFail()
	{
		return failed;
	}
}
