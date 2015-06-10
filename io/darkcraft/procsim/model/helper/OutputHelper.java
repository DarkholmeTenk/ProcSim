package io.darkcraft.procsim.model.helper;

import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OutputHelper
{
	private static boolean equal(IInstruction[][] a, IInstruction[][] b)
	{
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; i++)
		{
			if(a[i].length != b[i].length) return false;
			for(int j = 0; j < a[i].length; j++)
			{
				if((a[i][j] == null) ^ (b[i][j] == null)) return false;
				if(a[i][j] == null) continue;
				if(!a[i][j].equals(b[i][j]))
					return false;
			}
		}
		return true;
	}

	public static void output(AbstractSimulator sim)
	{
		output(sim.getStateNames(), sim.getMap(), sim.getInstructions());
	}

	public static void output(String[][] stateNames, List<IInstruction[][]> states, List<IInstruction> instructions)
	{
		List<List<String>> data = outputData(stateNames,states,instructions);
		for(int i = 0; i < data.size(); i++)
		{
			List<String> row = data.get(i);
			for(int j = 0; j < row.size(); j++)
			{
				String s = row.get(j);
				if(j == 0)
					System.out.format("%-25s|", s);
				else
					System.out.format("%3s|", s);
			}
			System.out.println();
		}
	}

	public static List<List<String>> outputData(AbstractSimulator sim)
	{
		return outputData(sim.getStateNames(), sim.getMap(), sim.getInstructions());
	}

	private static int exeIndex(String[] stateNames)
	{
		for(int i = 0; i < stateNames.length; i++)
		{
			if(stateNames.equals("EXE"))
				return i;
		}
		return 2;
	}

	public static List<List<String>> outputData(String[][] stateNames, List<IInstruction[][]> states, List<IInstruction> instructions)
	{
		List<List<String>> data = new ArrayList<List<String>>();
		for(int i = 0; i <= instructions.size(); i++)
		{
			data.add(new ArrayList<String>());
		}

		int row = 0;
		data.get(0).add("");
		HashSet<Integer> disabledStates = new HashSet<Integer>();
		int sameCounter = 0;
		for(int i = 0; i < states.size() - 1; i++)
		{
			IInstruction[][] state = states.get(i);
			//if(state.equals(states.get(i+1)))
			if(equal(state,states.get(i+1)))
				sameCounter++;
			else
				sameCounter = 0;
			if(sameCounter > 1)
				disabledStates.add(i);
		}

		int count = 0;
		boolean printedMissing = false;
		for(Object o : states)
		{
			if(!disabledStates.contains(count))
			{
				data.get(0).add("" + count);
				printedMissing = false;
			}
			else
			{
				if(!printedMissing)
					data.get(0).add("...");
				printedMissing = true;
			}
			count++;
		}
		row++;
		for(IInstruction instruction : instructions)
		{
			int prevSt = -1;
			boolean found = instruction.hasStarted();
			if(!found)
				continue;
			boolean d = false;
			List<String> rd = data.get(row);
			rd.add(instruction.toString());
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
				boolean disabled = disabledStates.contains(i);
				if(disabled && !d)
				{
					d = true;
					if(index == -1)
						rd.add("");
					else
						rd.add("...");
				}
				else if(!disabled)
				{
					d = false;
					if(index == -1)
						rd.add("");
					else
					{
						if(instruction.didLeaveEarly())
							rd.add("#" + stateNames[pipeline][index]);
						else if(instruction.didFail() && index >= exeIndex(stateNames[pipeline]))
							rd.add("*" + stateNames[pipeline][index]);
						else if(index == prevSt)
							rd.add("@" + stateNames[pipeline][index]);
						else
							rd.add(stateNames[pipeline][index]);
						prevSt = index;
					}
				}
			}
			row++;
		}
		for(int i = data.size()-1; i >= row; i--)
			data.remove(i);
		return data;
	}

	private static final String[] sizes = new String[]{"B", "KB", "MB"};
	public static String byteString(int size)
	{
		int i = 0;
		while(size >= 1024 && i < sizes.length)
		{
			size = size / 1024;
			i++;
		}
		return size + sizes[i];
	}
}
