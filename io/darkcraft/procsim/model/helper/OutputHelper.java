package io.darkcraft.procsim.model.helper;

import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.util.List;

public class OutputHelper
{
	public static void output(String[][] stateNames, List<IInstruction[][]> states, List<IInstruction> instructions)
	{
		System.out.format("%24s| ","");
		int count = 0;
		for(Object o : states)
		{
			System.out.format("%3d|", count++);
		}
		System.out.println();
		for(IInstruction instruction : instructions)
		{
			boolean found = false;
			for(IInstruction[][] inArAr : states)
				for(IInstruction[] inAr : inArAr)
					for(IInstruction in : inAr)
						if(in == instruction)
							found = true;
			if(!found)
				continue;
			System.out.format("%-24s| ",instruction.toString());
			for(int i = 0; i < states.size(); i++)
			{
				IInstruction[][] current = states.get(i);
				int pipeline = -1;
				int index = -1;
				pipelineSearch:
				for(int j = 0; j < current.length; j++)
				{
					for(int k = 0; k< current[j].length; k++)
					{
						if(current[j][k] == instruction)
						{
							pipeline = j;
							index = k;
							break pipelineSearch;
						}
					}
				}
				if(index == -1)
					System.out.format("%3s|", "");
				else
					System.out.format("%3s|", stateNames[pipeline][index]);
			}
			System.out.println("");
		}
	}

	public static void output(AbstractSimulator sim)
	{
		output(sim.getStateNames(), sim.getMap(), sim.getInstructions());
	}
}
