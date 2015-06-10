package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.instruction.instructions.Branch;

import java.util.List;

public class SuperScalarSimulator extends InOrderSimulator
{
	private IInstruction[] nexts;

	public SuperScalarSimulator(IMemory _mem, IRegisterBank _reg, InstructionReader _reader, AbstractPipeline... _pipelines)
	{
		super(_mem, _reg, _pipelines, _reader);
		nexts = new IInstruction[pipeline.length];
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
		stateTimeline.add(getState());
		for(AbstractPipeline pl : pipeline)
			if(!pl.isEmpty())
				allEmpty = false;
		for(AbstractPipeline pl : pipeline)
			pl.writeback(this);
		for(AbstractPipeline pl : pipeline)
			pl.memory(this);
		for(AbstractPipeline pl : pipeline)
			pl.execute(this);
		for(AbstractPipeline pl : pipeline)
			pl.instructionData(this);
		for(AbstractPipeline pl : pipeline)
			pl.instructionFetch(this);
		for(AbstractPipeline pl : pipeline)
			pl.moveForward(this);
		return !allEmpty;
	}

	private IInstruction assign(IInstruction i)
	{
		if(i != null)
		{
			for(AbstractPipeline p : pipeline)
			{
				List<String> dangerous = p.getDangerousOut();
				String[] input = i.getInputRegisters();
				if(input != null)
					for(String s : input)
						if(s != null && dangerous.contains(s)) return i;
				dangerous = p.getDangerousIn();
				if(i.getOutputRegister() != null && dangerous.contains(i.getOutputRegister())) return i;
			}
			for(AbstractPipeline p : pipeline)
			{
				if(p.addInstruction(i)) return null;
			}
		}
		return i;
	}

	@Override
	public void flushInstructionCache()
	{
		nexts = new IInstruction[nexts.length];
	}

	@Override
	protected void assignNext()
	{
		for(int i = 0; i < nexts.length - 1; i++)
		{
			for(int j = 1; j < (nexts.length - i); j++)
				if(nexts[i] == null && nexts[i+j] != null)
				{
					nexts[i] = nexts[i+j];
					nexts[i+j] = null;
					break;
				}
		}

		for(int i = 0; i < nexts.length; i++)
		{
			if(nexts[i] == null)
			{
				int pcval = reg.getValue("PC", null);
				nexts[i] = reader.get(pcval);
				if(nexts[i] != null)
					reg.incrementPC();
				else
					break;
			}
		}

		for(int i = 0; i< nexts.length; i++)
		{
			IInstruction in = nexts[i];
			if(in instanceof Branch && i != 0)
				break;
			nexts[i] = assign(in);
			if(nexts[i] != null) break;
		}
	}

	@Override
	public int getMaxPipelines()
	{
		return 4;
	}
}
