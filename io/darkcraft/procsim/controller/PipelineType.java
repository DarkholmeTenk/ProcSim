package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.pipelines.FiveStepPipeline;
import io.darkcraft.procsim.model.instruction.InstructionReader;

public enum PipelineType
{
	FIVESTEP("Five Step - 1 FU");

	public final String name;
	private PipelineType(String _name)
	{
		name = _name;
	}

	public String getName()
	{
		return name;
	}

	public AbstractPipeline construct(IMemory memory, IRegisterBank registers, InstructionReader reader)
	{
		switch(this)
		{
		case FIVESTEP: return new FiveStepPipeline(memory, registers, reader);
		default: return null;
		}
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
