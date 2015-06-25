package io.darkcraft.procsim.tests;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.memory.StubMem;
import io.darkcraft.procsim.model.components.pipelines.FiveStepPipeline;
import io.darkcraft.procsim.model.components.registerbank.StandardBank;
import io.darkcraft.procsim.model.helper.OutputHelper;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.model.simulator.InOrderSimulator;
import io.darkcraft.procsim.model.simulator.SuperScalarSimulator;

import java.io.File;

import org.junit.Test;

public class SimulatorTest
{
	@Test
	public void simpleTest()
	{
		InstructionReader read = new InstructionReader(new File("/home/mbax2sb4/workspace/ProcSim/src/test01.txt"));
		IMemory mem = new StubMem();
		IRegisterBank reg = new StandardBank(16);
		AbstractPipeline pipe = new FiveStepPipeline(mem,reg, read);

		AbstractSimulator sim = new InOrderSimulator(mem,reg,pipe,read);
		while(sim.step());
		System.out.println(reg.getValue("R3", null));
	}

	@Test
	public void printSuper()
	{
		System.out.println("2-Way Super scalar");
		InstructionReader read = new InstructionReader(new File("/home/mbax2sb4/workspace/ProcSim/src/test02.txt"));
		IMemory mem = new StubMem();
		IRegisterBank reg = new StandardBank(16);
		AbstractPipeline pipe = new FiveStepPipeline(mem,reg, read);
		AbstractPipeline pipeTwo = new FiveStepPipeline(mem,reg, read);

		AbstractSimulator sim = new SuperScalarSimulator(mem,reg,read, pipe, pipeTwo);
		while(sim.step());
		OutputHelper.output(sim,sim.getStateNames(), sim.getStateTimeline(), read.getAll());
	}

	@Test
	public void printSuperer()
	{
		System.out.println("4-Way Super scalar");
		InstructionReader read = new InstructionReader(new File("/home/mbax2sb4/workspace/ProcSim/src/test02.txt"));
		IMemory mem = new StubMem();
		IRegisterBank reg = new StandardBank(16);
		AbstractPipeline pipe = new FiveStepPipeline(mem,reg, read);
		AbstractPipeline pipeTwo = new FiveStepPipeline(mem,reg, read);
		AbstractPipeline pipeThr = new FiveStepPipeline(mem,reg, read);
		AbstractPipeline pipeFor = new FiveStepPipeline(mem,reg, read);

		AbstractSimulator sim = new SuperScalarSimulator(mem,reg,read, pipe, pipeTwo, pipeThr, pipeFor);
		while(sim.step());
		OutputHelper.output(sim,sim.getStateNames(), sim.getStateTimeline(), read.getAll());
	}

	@Test
	public void printOutput()
	{
		System.out.println("Single scalar");
		InstructionReader read = new InstructionReader(new File("/home/mbax2sb4/workspace/ProcSim/src/test02.txt"));
		IMemory mem = new StubMem();
		IRegisterBank reg = new StandardBank(16);
		AbstractPipeline pipe = new FiveStepPipeline(mem,reg, read);

		AbstractSimulator sim = new InOrderSimulator(mem,reg,pipe,read);
		while(sim.step());
		OutputHelper.output(sim,sim.getStateNames(), sim.getStateTimeline(), read.getAll());
	}
}
