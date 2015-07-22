package io.darkcraft.procsim.model.components.pipelines;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.helper.MiscFunctions;
import io.darkcraft.procsim.model.helper.Pair;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.instruction.instructions.Add;
import io.darkcraft.procsim.model.instruction.instructions.Mul;
import io.darkcraft.procsim.model.instruction.instructions.Sub;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

public class ThreeFunctionalUnitPipeline extends FiveStepPipeline
{
	public ThreeFunctionalUnitPipeline(IMemory _memory, IRegisterBank _registers, InstructionReader reader)
	{
		super(_memory, _registers, reader);
		wbStage = 9;
		memStage = 8;
	}

	public String[] stages = new String[]{"IF","ID","EX","ADD","ADD","MUL","MUL","MUL","MEM","WB"};
	int timer = 0;

	@Override
	protected boolean attemptToMove(int from, Integer to, boolean bReq)
	{
		if(to == null)
			pipeline.remove(from);
		if(!pipeline.containsKey(from)) return false;
		IInstruction toMove = pipeline.get(from).a;
		if(to == exStage)
		{
			if((toMove instanceof Add) || (toMove instanceof Sub))
				to = 3;
			if(toMove instanceof Mul)
				to = 5;
		}
		int block = MiscFunctions.in(getExeBlocks(), to);
		if((block != -1) && (MiscFunctions.in(getExeBlocks(), from) == -1))
		{
			int[] bl = getExeBlocks()[block];
			for(int i : bl)
				if(pipeline.containsKey(i))
					return false;
		}
		else if(pipeline.containsKey(to)) return false;
		if(bReq && !pipeline.get(from).b) return false;
		Pair<IInstruction, Boolean> newPair = new Pair(toMove,false);
		pipeline.put(to, newPair);
		pipeline.remove(from);
		return true;
	}

	@Override
	public String[] getPipelineStages()
	{
		return stages;
	}

	@Override
	public String[] getPipelineShorts()
	{
		return stages;
	}

	private void execute(int i, AbstractSimulator sim)
	{
		if((i == 2) || (i == 4) || (i == 7))
		{
			exStage = i;
			super.execute(sim);
		}
		else
		{
			exStage = 2;
			attemptToMove(i,i+1,false);
		}
		exStage = 2;
	}

	@Override
	public void stepStage(AbstractSimulator sim, int stage)
	{
		switch(stage)
		{
		case 9: writeback(sim); break;
		case 8: memory(sim); break;
		case 1: instructionData(sim); break;
		case 0: instructionFetch(sim); break;
		default: execute(stage, sim); break;
		}
	}

	@Override
	public boolean isFirstExeStage(int stage)
	{
		return (stage == 2) || (stage == 3) || (stage == 5);
	}

	private static int[][] exeBlocks = new int[][]{new int[]{7,6,5},new int[]{4,3}, new int[]{2}};
	@Override
	public int[][] getExeBlocks()
	{
		return exeBlocks;
	}

}
