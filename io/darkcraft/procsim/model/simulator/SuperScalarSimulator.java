package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

public class SuperScalarSimulator extends InOrderSimulator
{

	public SuperScalarSimulator(IMemory _mem, IRegisterBank _reg, InstructionReader _reader, AbstractPipeline... _pipelines)
	{
		super(_mem, _reg, _pipelines, _reader);
	}

	private IInstruction[][] getState()
	{
		IInstruction[][] states = new IInstruction[pipeline.length][];
		for(int i = 0; i < states.length; i++)
			states[i] = pipeline[i].getState();
		return states;
	}

	@Override
	protected boolean stepPipelines()
	{
		boolean allEmpty = true;
		for(AbstractPipeline pl : pipeline)
		if(!pl.isEmpty())
		{
			stateTimeline.add(getState());
			pl.step();
			allEmpty = false;
		}
		return !allEmpty;
	}

	@Override
	protected void assignNext()
	{
		if(next == null)
		{
			int pcval = reg.getValue("PC", null);
			if(next == null)
				next = reader.get(pcval);
			if(next != null)
				reg.incrementPC();
		}

		if(next != null)
		{
			for(AbstractPipeline p : pipeline)
			{
				if(p.addInstruction(next))
				{
					next = null;
					break;
				}
			}
		}
	}
}
