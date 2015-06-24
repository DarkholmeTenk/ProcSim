package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.helper.MiscFunctions;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

public class InOrderSimulator extends AbstractSimulator
{
	IInstruction next = null;


	public InOrderSimulator(IMemory _mem, IRegisterBank _reg, AbstractPipeline[] _pipeline, InstructionReader _reader)
	{
		super(_mem, _reg, _pipeline, _reader);
		reader.open();
	}

	public InOrderSimulator(IMemory _mem, IRegisterBank _reg, InstructionReader _reader, AbstractPipeline... pipes)
	{
		this(_mem,_reg,pipes,_reader);
	}

	public InOrderSimulator(IMemory _mem, IRegisterBank _reg, AbstractPipeline _pipeline, InstructionReader _reader)
	{
		super(_mem, _reg, new AbstractPipeline[]{_pipeline}, _reader);
		reader.open();
	}

	protected boolean stepPipelines()
	{
		if(!pipeline[0].isEmpty())
		{
			addStage();
			AbstractPipeline pl = pipeline[0];
			int length = pl.getPipelineStages().length;
			int[][] exeBlocks = pl.getExeBlocks();
			int max = MiscFunctions.max(exeBlocks);
			int min = MiscFunctions.min(exeBlocks);
			for(int i = length - 1; i > max; i--)
				pl.stepStage(this, i);
			stepUnsafe(exeBlocks);
			for(int i = min - 1; i >= 0; i--)
				pl.stepStage(this, i);
			pl.increaseTimer();
			return true;
		}
		return false;
	}

	protected void assignNext()
	{
		if(next == null)
		{
			int pcval = reg.getValue("PC", null);
			if(next == null)
			{
				next = reader.get(pcval);
				if(next != null)
					reg.incrementPC();
			}
		}

		if(next != null && pipeline[0].addInstruction(next))
			next = null;
	}

	@Override
	public boolean step()
	{
		assignNext();
		mem.step(this);
		if(!stepPipelines())
		{
			reader.close();
			return false;
		}
		return true;
	}

	@Override
	public int getMaxPipelines()
	{
		return 1;
	}

	@Override
	public void flushInstructionCache()
	{
		next = null;
	}

}
