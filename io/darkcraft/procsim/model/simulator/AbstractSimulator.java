package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

import java.util.ArrayList;

public abstract class AbstractSimulator
{
	protected final IMemory				mem;
	protected final IRegisterBank		reg;
	protected final AbstractPipeline[]	pipeline;
	protected final InstructionReader	reader;

	public AbstractSimulator(IMemory _mem, IRegisterBank _reg, AbstractPipeline[] _pipeline, InstructionReader _reader)
	{
		mem = _mem;
		reg = _reg;
		pipeline = _pipeline;
		reader = _reader;
	}

	public abstract boolean step();

	/**
	 * @return an array list with each entry representing the state of the pipeline at that time
	 */
	public abstract ArrayList<IInstruction[][]> getMap();

	public void clearUpTo(int i)
	{
		for(AbstractPipeline p : pipeline)
			p.clearUpTo(i);
	}
}
