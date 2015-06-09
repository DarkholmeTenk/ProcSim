package io.darkcraft.procsim.model.tests;

import static org.junit.Assert.assertTrue;
import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.memory.StandardMemory;
import io.darkcraft.procsim.model.components.memory.cache.DMCache;
import io.darkcraft.procsim.model.components.pipelines.FiveStepPipeline;
import io.darkcraft.procsim.model.components.registerbank.StandardBank;
import io.darkcraft.procsim.model.helper.OutputHelper;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.model.simulator.SuperScalarSimulator;

import java.io.File;

import org.junit.Test;

public class MemoryTest
{

	@Test
	public void initialTest()
	{
		InstructionReader reader = new InstructionReader(new File("/home/mbax2sb4/ProcSim/tests/cacheTest.txt"));
		IMemory memory = new StandardMemory(512,new File("/home/mbax2sb4/ProcSim/memory/basic.txt"));
		IRegisterBank registers = new StandardBank(16);
		AbstractPipeline pipeline = new FiveStepPipeline(memory, registers, reader);
		AbstractPipeline pipelineTwo = new FiveStepPipeline(memory, registers, reader);
		AbstractSimulator sim = new SuperScalarSimulator(memory,registers,reader, pipeline,pipelineTwo);
		while(sim.step());
		int location = memory.getLocation("memoryOut");
		int value = memory.getValue(null, location);
		assertTrue(value == 798);
		OutputHelper.output(sim);
	}

	@Test
	public void cacheTest()
	{
		InstructionReader reader = new InstructionReader(new File("/home/mbax2sb4/ProcSim/tests/cacheTest.txt"));
		IMemory memory = new StandardMemory(512,new File("/home/mbax2sb4/ProcSim/memory/basic.txt"));
		memory = new DMCache(8,2,memory);
		IRegisterBank registers = new StandardBank(16);
		AbstractPipeline pipeline = new FiveStepPipeline(memory, registers, reader);
		AbstractPipeline pipelineTwo = new FiveStepPipeline(memory, registers, reader);
		AbstractSimulator sim = new SuperScalarSimulator(memory,registers,reader, pipeline,pipelineTwo);
		while(sim.step());
		int location = memory.getLocation("memoryOut");
		int value = memory.getValue(null, location);
		OutputHelper.output(sim);
	}

}
