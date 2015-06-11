package io.darkcraft.procsim.model.tests;

import io.darkcraft.procsim.controller.DependencyGraphBuilder;
import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.memory.StubMem;
import io.darkcraft.procsim.model.components.pipelines.FiveStepPipeline;
import io.darkcraft.procsim.model.components.registerbank.StandardBank;
import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.model.simulator.InOrderSimulator;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class DependencyTest
{

	@Test
	public void test()
	{
		InstructionReader read = new InstructionReader(new File("/home/mbax2sb4/ProcSim/tests/dependencyTestSimpler.txt"));
		IMemory mem = new StubMem();
		IRegisterBank reg = new StandardBank(16);
		AbstractPipeline pipe = new FiveStepPipeline(mem,reg, read);

		AbstractSimulator sim = new InOrderSimulator(mem,reg,pipe,read);
		while(sim.step());
		List<IDependency> deps = DependencyGraphBuilder.getGraph(sim.getInstructions());
		for(IDependency dep : deps)
			System.out.println(dep);
	}

}
