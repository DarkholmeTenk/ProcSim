package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.model.simulator.InOrderSimulator;
import io.darkcraft.procsim.model.simulator.OoOExSimulator;
import io.darkcraft.procsim.model.simulator.OoOIssueSimulator;
import io.darkcraft.procsim.model.simulator.SuperScalarSimulator;

public enum SimulatorType
{
	INORDERSINGLE("In Order - Single Scalar",1),
	INORDERSUPER("In Order - Super Scalar",4),
	OOOE("Out of Order Execution",4),
	OOO("Out of Order",4,true);

	private final String name;
	public final int maxPipelines;
	public final boolean ooo;
	private SimulatorType(String n, int max)
	{
		this(n,max,false);
	}

	private SimulatorType(String n, int max, boolean outOfOrder)
	{
		name = n;
		maxPipelines = max;
		ooo = outOfOrder;
	}

	public AbstractSimulator getSimulator(IMemory mem, IRegisterBank reg, InstructionReader read, AbstractPipeline... pipe)
	{
		switch(this)
		{
			case INORDERSINGLE: return new InOrderSimulator(mem,reg,read,pipe);
			case INORDERSUPER: return new SuperScalarSimulator(mem,reg,read,pipe);
			case OOOE: return new OoOExSimulator(mem,reg,read,pipe);
			case OOO: return new OoOIssueSimulator(mem, reg, read, pipe);
			default: return null;
		}
	}

	public String getName()
	{
		return name;
	}

	public static SimulatorType get(String n)
	{
		for(SimulatorType s : values())
			if(s.getName().equals(n))
				return s;
		return null;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
