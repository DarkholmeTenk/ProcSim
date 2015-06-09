package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;

import java.util.ArrayList;

public class InOrderSimulator extends AbstractSimulator
{
	IInstruction next = null;
	ArrayList<IInstruction[][]> stateTimeline;

	public InOrderSimulator(IMemory _mem, IRegisterBank _reg, AbstractPipeline[] _pipeline, InstructionReader _reader)
	{
		super(_mem, _reg, _pipeline, _reader);
		reader.open();
		stateTimeline = new ArrayList<IInstruction[][]>();
	}

	public InOrderSimulator(IMemory _mem, IRegisterBank _reg, AbstractPipeline _pipeline, InstructionReader _reader)
	{
		super(_mem, _reg, new AbstractPipeline[]{_pipeline}, _reader);
		reader.open();
		stateTimeline = new ArrayList<IInstruction[][]>();
	}

	private IInstruction[][] getState()
	{
		return new IInstruction[][] {pipeline[0].getState()};
	}

	protected boolean stepPipelines()
	{
		if(!pipeline[0].isEmpty())
		{
			stateTimeline.add(getState());
			AbstractPipeline pl = pipeline[0];
			pl.writeback(this);
			pl.memory(this);
			pl.execute(this);
			pl.instructionData(this);
			pl.instructionFetch(this);
			pl.moveForward(this);
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
				next = reader.get(pcval);
			if(next != null)
				reg.incrementPC();
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
	public ArrayList<IInstruction[][]> getMap()
	{
		return stateTimeline;
	}

}
