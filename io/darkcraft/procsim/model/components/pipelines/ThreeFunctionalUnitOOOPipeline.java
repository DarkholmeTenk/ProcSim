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

public class ThreeFunctionalUnitOOOPipeline extends OneFunctionalUnitOOOPipeline
{

	public ThreeFunctionalUnitOOOPipeline(IMemory _memory, IRegisterBank _registers, InstructionReader reader)
	{
		super(_memory, _registers, reader);
		stages = new String[]{"EX","ADD","ADD","MUL","MUL","MUL","MEM","WB"};
		ifStage = -1;
		idStage = -1;
		exStage = 0;
		memStage = 6;
		wbStage = 7;
	}

	private void execute(int i, AbstractSimulator sim)
	{
		if((i == 0) || (i == 2) || (i == 5))
		{
			exStage = i;
			super.execute(sim);
		}
		else
		{
			exStage = 0;
			attemptToMove(i,i+1,false);
		}
		exStage = 0;
	}

	@Override
	public boolean addInstruction(IInstruction in)
	{
		if(pipeline.containsKey(exStage))
			return false;
		if(clearToAdd(in))
		{
			exStage = 0;
			if((in instanceof Add) || (in instanceof Sub))
				exStage = 1;
			if(in instanceof Mul)
				exStage = 3;
			int block = MiscFunctions.in(getExeBlocks(), exStage);
			if(block != -1)
			{
				int[] bl = getExeBlocks()[block];
				for(int i : bl)
					if(pipeline.containsKey(i))
						return false;
			}
			pipeline.put(exStage, new Pair<IInstruction, Boolean>(in,false));
			in.start(timer);
			return true;
		}
		return false;
	}

	@Override
	public void stepStage(AbstractSimulator sim, int stage)
	{
		switch(stage)
		{
		case 7: writeback(sim); break;
		case 6: memory(sim); break;
		default: execute(stage, sim); break;
		}
	}

	private static int[][] exeBlocks = new int[][]{new int[]{5,4,3},new int[]{2,1}, new int[]{0}};
	@Override
	public int[][] getExeBlocks()
	{
		return exeBlocks;
	}

}
