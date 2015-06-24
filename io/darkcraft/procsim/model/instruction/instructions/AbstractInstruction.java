package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;

public abstract class AbstractInstruction implements IInstruction
{
	public final Conditional	c;
	private int					startTime	= -1;
	private int					finishTime	= -1;
	private boolean				executed	= false;
	private boolean				failed		= false;
	private boolean				leftEarly	= false;
	public final int			id;

	public AbstractInstruction(Conditional _c, int _address)
	{
		c = _c;
		id = _address;
	}

	@Override
	public Conditional getConditional()
	{
		return c;
	}

	@Override
	public void start(int _startTime)
	{
		startTime = _startTime;
	}

	@Override
	public boolean hasStarted()
	{
		return startTime > -1;
	}

	@Override
	public int getStartTime()
	{
		return startTime;
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

	protected String getID()
	{
		return String.format("[%03d] ", id << 2);
	}

	@Override
	public void leaveEarly()
	{
		leftEarly = true;
	}

	@Override
	public boolean didLeaveEarly()
	{
		return leftEarly;
	}

	@Override
	public void finish(int time)
	{
		finishTime = time;
	}

	@Override
	public int getFinishTime()
	{
		return finishTime;
	}
}
