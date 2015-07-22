package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.pipelines.FiveStepPipeline;
import io.darkcraft.procsim.model.components.pipelines.OneFunctionalUnitOOOPipeline;
import io.darkcraft.procsim.model.components.pipelines.ThreeFunctionalUnitOOOPipeline;
import io.darkcraft.procsim.model.components.pipelines.ThreeFunctionalUnitPipeline;
import io.darkcraft.procsim.model.instruction.InstructionReader;

import java.util.ArrayList;

public enum PipelineType
{
	FIVESTEP("Five Step - 1 FU"),
	FIVESTEP3("Five Step - 3 FU"),
	OOOONEFU("1 FU", true),
	OOOTHRFU("3 FU", true);

	public final String name;
	public final boolean ooo;
	private PipelineType(String _name)
	{
		this(_name,false);
	}

	private PipelineType(String _name, boolean outOfOrder)
	{
		name = _name;
		ooo = outOfOrder;
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
		case FIVESTEP3: return new ThreeFunctionalUnitPipeline(memory, registers, reader);
		case OOOONEFU: return new OneFunctionalUnitOOOPipeline(memory, registers, reader);
		case OOOTHRFU: return new ThreeFunctionalUnitOOOPipeline(memory, registers, reader);
		default: return null;
		}
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public static PipelineType get(String name)
	{
		for(PipelineType p : values())
			if(p.getName().equals(name))
				return p;
		return null;
	}

	private static PipelineType[] trueArray = null;
	private static PipelineType[] falseArray = null;
	public static PipelineType[] getValues(boolean outOfOrder)
	{
		if((trueArray == null) || (falseArray == null))
		{
			ArrayList<PipelineType> trueList = new ArrayList();
			ArrayList<PipelineType> falseList = new ArrayList();
			for(PipelineType pt : values())
			{
				if(pt.ooo)
					trueList.add(pt);
				else
					falseList.add(pt);
			}
			trueArray = new PipelineType[trueList.size()];
			falseArray = new PipelineType[falseList.size()];
			for(int i = 0; i < trueList.size(); i++)
				trueArray[i] = trueList.get(i);
			for(int i = 0; i < falseList.size(); i++)
				falseArray[i] = falseList.get(i);
		}
		if(outOfOrder)
			return trueArray;
		return falseArray;
	}
}
