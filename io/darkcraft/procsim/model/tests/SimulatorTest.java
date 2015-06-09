package io.darkcraft.procsim.model.tests;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.memory.StubMem;
import io.darkcraft.procsim.model.components.pipelines.FiveStepPipeline;
import io.darkcraft.procsim.model.components.registerbank.StandardBank;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.model.simulator.InOrderSimulator;
import io.darkcraft.procsim.model.simulator.SuperScalarSimulator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
		System.out.println("Super scalar");
		InstructionReader read = new InstructionReader(new File("/home/mbax2sb4/workspace/ProcSim/src/test02.txt"));
		IMemory mem = new StubMem();
		IRegisterBank reg = new StandardBank(16);
		AbstractPipeline pipe = new FiveStepPipeline(mem,reg, read);
		AbstractPipeline pipeTwo = new FiveStepPipeline(mem,reg, read);

		AbstractSimulator sim = new SuperScalarSimulator(mem,reg,read, pipe, pipeTwo);
		while(sim.step());
		String[] stateNames = pipe.getPipelineShorts();
		ArrayList<IInstruction[][]> states = sim.getMap();
		List<IInstruction> instructions = read.getAll();
		for(IInstruction instruction : instructions)
		{
			System.out.format("%-24s| ",instruction.toString());
			for(int i = 0; i < states.size(); i++)
			{
				IInstruction[][] current = states.get(i);
				int index = -1;
				for(int j = 0; j < current.length; j++)
				{
					for(int k = 0; k< current[j].length; k++)
					{
						if(current[j][k] == instruction)
						{
							index = k;
							break;
						}
					}
				}
				if(index == -1)
					System.out.format("%4s|", "");
				else
					System.out.format("%4s|", stateNames[index]);
			}
			System.out.println("");
		}
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
		String[] stateNames = pipe.getPipelineShorts();
		ArrayList<IInstruction[][]> states = sim.getMap();
		List<IInstruction> instructions = read.getAll();
		for(IInstruction instruction : instructions)
		{
			System.out.format("%-24s| ",instruction.toString());
			for(int i = 0; i < states.size(); i++)
			{
				IInstruction[][] current = states.get(i);
				int index = -1;
				for(int j = 0; j < current.length; j++)
				{
					for(int k = 0; k< current[j].length; k++)
					{
						if(current[j][k] == instruction)
						{
							index = k;
							break;
						}
					}
				}
				if(index == -1)
					System.out.format("%4s|", "");
				else
					System.out.format("%4s|", stateNames[index]);
			}
			System.out.println("");
		}
	}
}
