package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.helper.MiscFunctions;
import io.darkcraft.procsim.model.helper.PipelineComparator;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

import java.util.Arrays;
import java.util.Comparator;

public class SuperScalarSimulator extends InOrderSimulator
{
	private IInstruction[] nexts;
	protected Comparator<AbstractPipeline>[] comparator;

	public SuperScalarSimulator(IMemory _mem, IRegisterBank _reg, InstructionReader _reader, AbstractPipeline... _pipelines)
	{
		super(_mem, _reg, _pipelines, _reader);
		nexts = new IInstruction[pipeline.length];
		comparator = new Comparator[_pipelines[0].getPipelineStages().length];
		for(int i = 0; i < comparator.length; i++)
			comparator[i] = new PipelineComparator(i);
	}

	@Override
	protected boolean stepPipelines()
	{
		boolean allEmpty = true;
		for(AbstractPipeline pl : pipeline)
			if(!pl.isEmpty())
				allEmpty = false;
		if(!allEmpty)
			addStage();
		int length = pipeline[0].getPipelineStages().length;
		int[][] exeBlocks = pipeline[0].getExeBlocks();
		int max = MiscFunctions.max(exeBlocks);
		int min = MiscFunctions.min(exeBlocks);
		for(int stage = length -1; stage> max; stage--)
		{
			Arrays.sort(pipeline, comparator[stage]);
			for(AbstractPipeline p : pipeline)
			{
				p.stepStage(this, stage);
				if(p.getInstruction(stage) != null) break;
			}
		}
		stepUnsafe(exeBlocks);
		for(int stage = min - 1; stage >= 0; stage--)
		{
			Arrays.sort(pipeline, comparator[stage]);
			for(AbstractPipeline p : pipeline)
			{
				p.stepStage(this, stage);
				if(p.getInstruction(stage) != null) break;
			}
		}

		return !allEmpty;
	}

	private IInstruction assign(IInstruction i)
	{
		if(i != null)
		{
			for(AbstractPipeline p : pipeline)
			{
				if(p.addInstruction(i))
				{
					for(AbstractPipeline pl : pipeline)
						pl.increaseTimer();
					return null;
				}
			}
		}
		return i;
	}

	@Override
	public void flushInstructionCache(int address)
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
			if(in == null) break;
			//if(in instanceof Branch && i != 0)
			//	break;
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
