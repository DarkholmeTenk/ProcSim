package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.controller.DependencyType;
import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSimulator
{
	protected final IMemory				mem;
	protected final IRegisterBank		reg;
	protected final AbstractPipeline[]	pipeline;
	protected final InstructionReader	reader;

	/**
	 * Instantiates the important variables in an abstract simulator.
	 * @param _mem the memory to be used as the closest memory/cache to the processor
	 * @param _reg the register bank to use
	 * @param _pipeline the pipelines to use
	 * @param _reader the instruction reader to use
	 */
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

	/**
	 * Steps all of the functional units forwards, while ensuring that instructions finish execution in order.
	 * @param exeBlocks a 2D array of (functional unit, step in functional unit)
	 */
	protected void stepUnsafe(int[][] exeBlocks)
	{
		boolean[][] done = new boolean[pipeline.length][exeBlocks.length];
		for(int count = 0; count < (pipeline.length * exeBlocks.length); count++)
		{
			int lowestVal = Integer.MAX_VALUE;
			int lowestPL = -1;
			int lowestEB = -1;
			int lowestIB = -1;
			for(int plI = 0; plI < pipeline.length; plI++)
			{
				for(int ebI = 0; ebI < exeBlocks.length; ebI++)
				{
					if(done[plI][ebI]) continue;
					IInstruction inst = null;
					int ibI = -1;
					while(inst == null && (++ibI) < exeBlocks[ebI].length)
						inst = pipeline[plI].getInstruction(exeBlocks[ebI][ibI]);
					if(inst == null) continue;
					if(inst.getStartTime() < lowestVal)
					{
						lowestVal = inst.getStartTime();
						lowestPL = plI;
						lowestEB = ebI;
						lowestIB = ibI;
					}
				}
			}
			if(lowestPL == -1) break;
			if(lowestIB >= 1) break;
			done[lowestPL][lowestEB] = true;
			int stage = exeBlocks[lowestEB][0];
			pipeline[lowestPL].stepStage(this, stage);
			if(pipeline[lowestPL].getInstruction(stage) != null) break;
		}
		for(AbstractPipeline pl: pipeline)
		{
			for(int[] x : exeBlocks)
			{
				for(int i = 1; i < x.length; i++)
					pl.stepStage(this, x[i]);
			}
		}

	}

	/**
	 * Causes a cycle to happen. Assigns a new instruction. Steps the pipelines forwards once.
	 */
	public abstract boolean step();

	/**
	 * @return A 2D array of (pipeline, stage) which lists all the names of all the possible stages.
	 */
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
	 * Clears every instruction in all of the pipelines which comes after step i
	 * @param i
	 */
	public void clearAfter(int i)
	{
		for(AbstractPipeline p : pipeline)
			p.clearAfter(i);
	}

	/**
	 * Forces the simulator to dump its cache of instructions. Used by branches.
	 */
	public abstract void flushInstructionCache();

	/**
	 * @return a list containing all of the instructions which have passed through this simulator
	 */
	public List<IInstruction> getInstructions()
	{
		List<IInstruction> insts = new ArrayList(reader.getAll());
		Iterator<IInstruction> iter = insts.iterator();
		while(iter.hasNext())
		{
			IInstruction i = iter.next();
			if(!i.hasStarted())
				iter.remove();
		}
		return reader.getAll();
	}

	/**
	 * @return the maximum number of pipelines this simulator should be able to have.
	 */
	public abstract int getMaxPipelines();

	/**
	 * Used to identify which type of dependency arrows to display for stalls.
	 * @param dep the dependency type to check
	 * @return true if the dependency type's arrows should be displayed.
	 */
	public boolean isImportant(DependencyType dep)
	{
		switch(dep)
		{
		case RAW: return true;
		default: return false;
		}
	}

	/**
	 * Returns the last ID stage in pipeline pl
	 * @param pl
	 */
	public int getLastIDStage(int pl)
	{
		return pipeline[pl].getLastIDStage();
	}

	/**
	 * Used for highlighting stall dependencies.
	 * @param pl
	 * @param stage
	 * @return true if stage is one of the first executable stages in pipeline pl
	 * @throws IndexOutOfBoundsException if pl >= number of pipelines
	 */
	public boolean isExeStage(int pl, int stage)
	{
		return pipeline[pl].isFirstExeStage(stage);
	}
}
