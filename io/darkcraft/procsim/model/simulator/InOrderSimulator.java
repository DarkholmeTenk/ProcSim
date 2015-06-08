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
	ArrayList<IInstruction[]> stateTimeline;

	public InOrderSimulator(IMemory _mem, IRegisterBank _reg, AbstractPipeline _pipeline, InstructionReader _reader)
	{
		super(_mem, _reg, _pipeline, _reader);
		reader.open();
		stateTimeline = new ArrayList<IInstruction[]>();
	}

	@Override
	public boolean step()
	{
		if(next == null)
		{
			int pcval = reg.getValue("PC");
			if(next == null)
				next = reader.get(pcval);
			if(next != null)
				reg.incrementPC();
		}

		if(next != null && pipeline.addInstruction(next))
			next = null;
		if(!pipeline.isEmpty())
		{
			stateTimeline.add(pipeline.getState());
			pipeline.step();
		}
		else
		{
			reader.close();
			return false;
		}
		return true;
	}

	@Override
	public ArrayList<IInstruction[]> getMap()
	{
		return stateTimeline;
	}

}
