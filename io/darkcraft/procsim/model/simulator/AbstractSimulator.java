package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSimulator
{
	protected final IMemory				mem;
	protected final IRegisterBank		reg;
	protected final AbstractPipeline[]	pipeline;
	protected final InstructionReader	reader;

	public AbstractSimulator(IMemory _mem, IRegisterBank _reg, AbstractPipeline[] _pipeline, InstructionReader _reader)
	{
		mem = _mem;
		mem.read();
		reg = _reg;
		if(_pipeline.length > getMaxPipelines())
		{
			AbstractPipeline[] newPipelines = new AbstractPipeline[getMaxPipelines()];
			for(int i = 0; i < newPipelines.length; i++)
				newPipelines[i] = _pipeline[i];
			_pipeline = newPipelines;
		}
		pipeline = _pipeline;
		reader = _reader;
	}

	public abstract boolean step();

	public String[][] getStateNames()
	{
		String[][] stateNames = new String[pipeline.length][];
		for(int i = 0; i < stateNames.length; i++)
			stateNames[i] = pipeline[i].getPipelineShorts();
		return stateNames;
	}

	/**
	 * @return an array list with each entry representing the state of the pipeline at that time
	 */
	public abstract ArrayList<IInstruction[][]> getMap();

	/**
	 * Clears every instruction in the pipeline which comes after step i
	 * @param i
	 */
	public void clearAfter(int i)
	{
		for(AbstractPipeline p : pipeline)
			p.clearAfter(i);
	}

	public void flushInstructionCache()
	{
	}

	public List<IInstruction> getInstructions()
	{
		return reader.getAll();
	}

	public abstract int getMaxPipelines();
}
