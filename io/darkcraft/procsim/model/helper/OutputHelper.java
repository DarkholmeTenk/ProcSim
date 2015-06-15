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
		List<List<Pair<IInstruction,String>>> data = outputData(stateNames,states,instructions);
		for(int i = 0; i < data.size(); i++)
		{
			List<Pair<IInstruction,String>> row = data.get(i);
			for(int j = 0; j < row.size(); j++)
			{
				Pair<IInstruction,String> d = row.get(j);
				String s = d.b;
				if(j == 0)
					System.out.format("%-25s|", s);
				else
					System.out.format("%3s|", s);
			}
			System.out.println();
		}
	}

	public static List<List<Pair<IInstruction,String>>> outputData(AbstractSimulator sim)
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

	public static List<List<Pair<IInstruction,String>>> outputData(String[][] stateNames, List<IInstruction[][]> states, List<IInstruction> instructions)
	{
		List<List<Pair<IInstruction,String>>> data = new ArrayList<List<Pair<IInstruction,String>>>();
		for(int i = 0; i <= instructions.size(); i++)
		{
			data.add(new ArrayList<Pair<IInstruction,String>>());
		}

		int row = 0;
		data.get(0).add(new Pair(null,""));
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

		boolean printedMissing = false;
		for(int count = 0; count < states.size(); count++)
		{
			if(!disabledStates.contains(count))
			{
				data.get(0).add(new Pair(null,"" + count));
				printedMissing = false;
			}
			else
			{
				if(!printedMissing)
					data.get(0).add(new Pair(null,"..."));
				printedMissing = true;
			}
		}
		row++;
		for(IInstruction instruction : instructions)
		{
			int prevIndex = -1;
			if(!instruction.hasStarted())
				continue;
			boolean hasPrintedDisabled = false;
			List<Pair<IInstruction,String>> rd = data.get(row);
			rd.add(new Pair(instruction,instruction.toString()));
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
				if(disabled && !hasPrintedDisabled)
				{
					hasPrintedDisabled = true;
					if(index == -1)
						rd.add(null);
					else
						rd.add(new Pair(instruction,"..."));
				}
				else if(!disabled)
				{
					hasPrintedDisabled = false;
					if(index == -1)
						rd.add(null);
					else
					{
						if(instruction.didLeaveEarly())
							rd.add(new Pair(instruction,"#" + stateNames[pipeline][index]));
						else if(instruction.didFail() && index >= exeIndex(stateNames[pipeline]))
							rd.add(new Pair(instruction,"*" + stateNames[pipeline][index]));
						else if(index == prevIndex)
							rd.add(new Pair(instruction,"@" + stateNames[pipeline][index]));
						else
							rd.add(new Pair(instruction, stateNames[pipeline][index]));
						prevIndex = index;
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
