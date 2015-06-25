package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.controller.DependencyGraphBuilder;
import io.darkcraft.procsim.controller.DependencyType;
import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.helper.MiscFunctions;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OoOExSimulator extends SuperScalarSimulator
{

	public OoOExSimulator(IMemory _mem, IRegisterBank _reg, InstructionReader _reader, AbstractPipeline... _pipelines)
	{
		super(_mem, _reg, _reader, _pipelines);
	}

	/**
	 * @param passing the instruction which wants to get ahead
	 * @param toPass the instruction it wants to get ahead of
	 * @return true if passing can pass toPass
	 */
	@Override
	protected boolean canInstructionPass(IInstruction passing, IInstruction toPass)
	{
		if(passing.equals(toPass) || toPass == null) return true;
		//If passing is older than toPass then of course it can go in front because that would be in order
				if(passing.getStartTime() < toPass.getStartTime())
					return true;
		List<IInstruction> insts = new ArrayList<IInstruction>(2);
		insts.add(passing);
		insts.add(toPass);
		List<IDependency> deps = DependencyGraphBuilder.getGraphNew(insts);
		if(deps.size() == 0) return true;
		return false;
	}

	private void stepPipelines(int stage)
	{
		Arrays.sort(pipeline, comparator[stage]);
		extPlLoop:
		for(AbstractPipeline p : pipeline)
		{
			IInstruction passing = p.getInstruction(stage);
			if(passing == null) continue;
			for(AbstractPipeline x : pipeline)
			{
				if(x == p) continue;
				IInstruction toPass = x.getInstruction(stage);
				if(toPass == null) continue;
				if(!canInstructionPass(passing,toPass))
					continue extPlLoop;
			}
			p.stepStage(this, stage);
		}
	}

	@Override
	protected void stepUnsafe(int[][] exeBlocks)
	{
		boolean[][] done = new boolean[pipeline.length][exeBlocks.length];
		mainLoop:
		for (int count = 0; count < (pipeline.length * exeBlocks.length); count++)
		{
			int lowestVal = Integer.MAX_VALUE;
			int lowestPL = -1;
			int lowestEB = -1;
			int lowestIB = -1;
			for (int plI = 0; plI < pipeline.length; plI++)
			{
				for (int ebI = 0; ebI < exeBlocks.length; ebI++)
				{
					if (done[plI][ebI])
						continue;
					IInstruction inst = null;
					int ibI = -1;
					while (inst == null && (++ibI) < exeBlocks[ebI].length)
						inst = pipeline[plI].getInstruction(exeBlocks[ebI][ibI]);
					if (inst == null)
						continue;
					if (inst.getStartTime() < lowestVal)
					{
						lowestVal = inst.getStartTime();
						lowestPL = plI;
						lowestEB = ebI;
						lowestIB = ibI;
					}
				}
			}
			if (lowestPL == -1)
				break;
			if (lowestIB >= 1)
				break;
			done[lowestPL][lowestEB] = true;
			int stage = exeBlocks[lowestEB][0];
			IInstruction passing = pipeline[lowestPL].getInstruction(stage);
			if(passing == null) throw new RuntimeException ("wut");
			for(AbstractPipeline pl : pipeline)
				for(int[] tempArr : exeBlocks)
					for(int st : tempArr)
					{
						IInstruction toPass = pl.getInstruction(st);
						if(!canInstructionPass(passing, toPass))
							continue mainLoop;
					}
			pipeline[lowestPL].stepStage(this, stage);
		}
		for (AbstractPipeline pl : pipeline)
		{
			for (int[] x : exeBlocks)
			{
				for (int i = 1; i < x.length; i++)
					pl.stepStage(this, x[i]);
			}
		}

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
			stepPipelines(stage);
		}
		stepUnsafe(exeBlocks);
		for(int stage = min - 1; stage >= 0; stage--)
		{
			stepPipelines(stage);
		}

		return !allEmpty;
	}

	@Override
	public boolean isImportant(DependencyType dep)
	{
		return true;
	}
}
