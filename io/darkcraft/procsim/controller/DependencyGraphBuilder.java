package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.dependencies.RAW;
import io.darkcraft.procsim.model.dependencies.WAR;
import io.darkcraft.procsim.model.dependencies.WAW;
import io.darkcraft.procsim.model.helper.MapList;
import io.darkcraft.procsim.model.helper.Pair;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.instructions.Branch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DependencyGraphBuilder
{
	private static MapList<Pair<IInstruction,IInstruction>,IDependency> buildTotalMap(List<IDependency> dependencies)
	{
		MapList<Pair<IInstruction,IInstruction>,IDependency> total = new MapList();
		for(IDependency dependency : dependencies)
		{
			IInstruction from = dependency.getFrom();
			IInstruction to = dependency.getTo();
			Pair<IInstruction,IInstruction> pair = new Pair<IInstruction,IInstruction>(from,to);
			total.add(pair,dependency);
		}
		return total;
	}

	private static List<IDependency> removeTransitives(List<IDependency> dependencies, List<IInstruction> insts)
	{
		MapList<Pair<IInstruction,IInstruction>,IDependency> map = buildTotalMap(dependencies);
		for(Pair<IInstruction,IInstruction> pair : map.keySet())
		{
			IInstruction from = pair.a;
			IInstruction to = pair.b;
			for(IInstruction i : insts)
			{
				if(i.equals(from) || i.equals(to)) continue;
				Pair<IInstruction,IInstruction> pairTwo = new Pair(to,i);
				if(!map.containsKey(pairTwo)) continue;
				Pair<IInstruction,IInstruction> pairThree = new Pair(from,i);
				if(!map.containsKey(pairThree)) continue;
				List<IDependency> depListOne = map.getList(pairTwo);
				List<IDependency> depListTwo = map.getList(pairThree);
				for(IDependency a : depListOne)
					for(IDependency b : depListTwo)
						if(a.getType().equals(b.getType()))
							dependencies.remove(b);
			}
		}
		return dependencies;
	}

	private static String[] getInputRegisters(IInstruction in)
	{
		String[] inputs = in.getInputRegisters();
		if(inputs == null)
			inputs = new String[]{};
		if(in.getConditional() != Conditional.AL)
		{
			String[] newInputs = new String[inputs.length+1];
			if(inputs.length > 0)
				for(int i = 0; i < inputs.length; i++)
					newInputs[i] = inputs[i];
			newInputs[inputs.length]= "CPSR";
			inputs = newInputs;
		}
		return inputs;
	}

	public static List<IDependency> getDependencies(IInstruction from, IInstruction to)
	{
		ArrayList<IDependency> dependencies = new ArrayList<IDependency>();
		String aOut = from.getOutputRegister();
		String[] aInputs = getInputRegisters(from);
		if(from == to) return dependencies;
		String bOut = to.getOutputRegister();
		String[] bInputs = getInputRegisters(to);
		if(aOut != null)
		{
			for(String bInput : bInputs)
				if(bInput != null && aOut.equals(bInput))
				{
					dependencies.add(new RAW(from, to));
					break;
				}
			if(aOut.equals(bOut))
				dependencies.add(new WAW(from, to));
		}
		if(bOut != null)
		{
			for(String aInput : aInputs)
				if(aInput != null && bOut.equals(aInput))
				{
					dependencies.add(new WAR(from, to));
					break;
				}
		}
		return dependencies;
	}

	private static MapList<Pair<IInstruction,IInstruction>,IDependency> getAllConnectionsNew(List<IInstruction> instructions)
	{
		MapList<Pair<IInstruction,IInstruction>,IDependency> map = new MapList();
		//A map where the key is the instruction being linked to
		//and the value is a list of all the instructions that link to it.
		MapList<IInstruction,IInstruction> reverseMap = new MapList();
		for(int i = 0; i < instructions.size(); i++)
		{
			IInstruction a = instructions.get(i);
			String aOut = a.getOutputRegister();
			String[] aInputs = getInputRegisters(a);
			for(int j = i+1; j < instructions.size(); j++)
			{
				IInstruction b = instructions.get(j);
				if(a == b) continue;
				String bOut = b.getOutputRegister();
				String[] bInputs = getInputRegisters(b);
				List<IDependency> dependencies = new ArrayList<IDependency>(3);
				if(aOut != null)
				{
					for(String bInput : bInputs)
						if(bInput != null && aOut.equals(bInput))
						{
							dependencies.add(new RAW(a, b));
							break;
						}
					if(aOut.equals(bOut))
						dependencies.add(new WAW(a,b));
				}
				if(bOut != null)
				{
					for(String aInput : aInputs)
						if(aInput != null && bOut.equals(aInput))
						{
							dependencies.add(new WAR(a, b));
							break;
						}
				}
				if(dependencies.size() == 0)continue;
				Pair<IInstruction,IInstruction> newPair = new Pair(a,b);
				map.put(newPair,dependencies);
				if(!reverseMap.containsKey(b))
				{
					reverseMap.add(b, a);
					continue;
				}
				Iterator<IInstruction> fromIter = reverseMap.iterator(b);
				while(fromIter.hasNext())
				{
					IInstruction from = fromIter.next();
					Pair<IInstruction,IInstruction> pairOne = new Pair(from, b);
					Pair<IInstruction,IInstruction> pairTwo = new Pair(from, a);
					List<IDependency> dependenciesOne = map.getList(pairOne);
					List<IDependency> dependenciesTwo = map.getList(pairTwo);
					if(dependenciesOne == null)
					{
						fromIter.remove();
						continue;
					}
					if(dependenciesTwo == null) continue;
					for(IDependency dOne : dependencies)	//For all the dependencies we're adding (a->b)
					{
						for(IDependency dTwo : dependenciesTwo)	//For all the dependencies which lead to current start (from->a)
						{
							if(dOne.getType() != dTwo.getType()) continue;	//If they are not the same, try the next combo
							Iterator<IDependency> iter = dependenciesOne.iterator();
							while(iter.hasNext())	//For all the ones leading to current end (from->b)
							{
								IDependency oldD = iter.next();
								if(oldD.getType() == dOne.getType())		//If it's the same type as the other 2 deps
									iter.remove();							//Remove it (from->b leaving from->a and a->b)
							}
						}
					}
					if(dependenciesOne.size() == 0)
					{
						map.remove(pairOne);
						fromIter.remove();
					}
				}
				reverseMap.add(b,a);
			}
		}
		return map;
	}

	private static List<IDependency> getAllConnections(List<IInstruction> instructions)
	{
		ArrayList<IDependency> dependencies = new ArrayList<IDependency>();
		for(int i = 0; i < instructions.size() - 1; i++)
		{
			IInstruction a = instructions.get(i);
			String aOut = a.getOutputRegister();
			String[] aInputs = getInputRegisters(a);
			for(int j = i+1; j < instructions.size(); j++)
			{
				IInstruction b = instructions.get(j);
				if(a == b) continue;
				String bOut = b.getOutputRegister();
				String[] bInputs = getInputRegisters(b);
				if(aOut != null)
				{
					for(String bInput : bInputs)
						if(bInput != null && aOut.equals(bInput))
							dependencies.add(new RAW(a, b));
					if(aOut.equals(bOut))
						dependencies.add(new WAW(a,b));
				}
				if(bOut != null)
				{
					for(String aInput : aInputs)
						if(aInput != null && bOut.equals(aInput))
							dependencies.add(new WAR(a, b));
				}
			}
		}
		return dependencies;
	}

	public static List<IDependency> getGraph(List<IInstruction> instructions)
	{
		List<IDependency> dependencies = getAllConnections(instructions);
		dependencies = removeTransitives(dependencies, instructions);

		return dependencies;
	}

	public static List<IDependency> getGraphNew(List<IInstruction> instructions)
	{
		MapList<Pair<IInstruction,IInstruction>, IDependency> map = getAllConnectionsNew(instructions);
		return map.flatten();
	}

	/**
	 * Return true if the second instruction is dependent on the first.
	 * @param first
	 * @param second
	 * @return
	 */
	public static Boolean isDependant(IInstruction first, IInstruction second)
	{
		String aOut = first.getOutputRegister();
		String[] aInputs = getInputRegisters(first);
		String bOut = second.getOutputRegister();
		String[] bInputs = getInputRegisters(second);
		if(aOut != null)
		{
			for(String bInput : bInputs)
				if(bInput != null && aOut.equals(bInput))
					return true;
			if(aOut.equals(bOut))
				return true;
		}
		if(bOut != null)
		{
			for(String aInput : aInputs)
				if(aInput != null && bOut.equals(aInput))
					return true;
		}
		if(second instanceof Branch) return true;
		return false;
	}

	private static MapList<IInstruction,IDependency> getDirDependencies(List<IInstruction>insts, boolean to)
	{
		MapList<IInstruction,IDependency> map = new MapList<IInstruction,IDependency>();
		List<IDependency> dependencies = getGraphNew(insts);
		for(IDependency d : dependencies)
		{
			IInstruction k = to ? d.getTo() : d.getFrom();
			map.add(k,d);
		}
		return map;
	}

	public static MapList<IInstruction,IDependency> getToDependencies(List<IInstruction> insts)
	{
		return getDirDependencies(insts,true);
	}
}
