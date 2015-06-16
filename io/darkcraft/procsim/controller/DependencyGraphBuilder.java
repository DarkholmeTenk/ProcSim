package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.dependencies.RAW;
import io.darkcraft.procsim.model.dependencies.WAR;
import io.darkcraft.procsim.model.dependencies.WAW;
import io.darkcraft.procsim.model.helper.Pair;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.instructions.Branch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DependencyGraphBuilder
{
	private static HashMap<Pair<IInstruction,IInstruction>,List<IDependency>> buildTotalMap(List<IDependency> dependencies)
	{
		HashMap<Pair<IInstruction,IInstruction>,List<IDependency>> total = new HashMap();
		for(IDependency dependency : dependencies)
		{
			IInstruction from = dependency.getFrom();
			IInstruction to = dependency.getTo();
			Pair<IInstruction,IInstruction> pair = new Pair<IInstruction,IInstruction>(from,to);
			if(!total.containsKey(pair))
				total.put(pair, new ArrayList<IDependency>());
			List<IDependency> list = total.get(pair);
			list.add(dependency);
		}
		return total;
	}

	private static List<IDependency> removeTransitives(List<IDependency> dependencies, List<IInstruction> insts)
	{
		HashMap<Pair<IInstruction,IInstruction>,List<IDependency>> map = buildTotalMap(dependencies);
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
				List<IDependency> depListOne = map.get(pairTwo);
				List<IDependency> depListTwo = map.get(pairThree);
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

	private static Map<Pair<IInstruction,IInstruction>,List<IDependency>> getAllConnectionsNew(List<IInstruction> instructions)
	{
		Map<Pair<IInstruction,IInstruction>,List<IDependency>> map = new HashMap();
		//A map where the key is the instruction being linked to
		//and the value is a list of all the instructions that link to it.
		Map<IInstruction,List<IInstruction>> reverseMap = new HashMap();
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
					List<IInstruction> tempList = new ArrayList<IInstruction>();
					tempList.add(a);
					reverseMap.put(b,tempList);
					continue;
				}
				List<IInstruction> tempList = reverseMap.get(b);
				Iterator<IInstruction> fromIter = tempList.iterator();
				while(fromIter.hasNext())
				{
					IInstruction from = fromIter.next();
					Pair<IInstruction,IInstruction> pairOne = new Pair(from, b);
					Pair<IInstruction,IInstruction> pairTwo = new Pair(from, a);
					List<IDependency> dependenciesOne = map.get(pairOne);
					List<IDependency> dependenciesTwo = map.get(pairTwo);
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
				tempList.add(a);
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
		List<IDependency> dependencies = new ArrayList<IDependency>();
		Map<Pair<IInstruction,IInstruction>, List<IDependency>> map = getAllConnectionsNew(instructions);
		for(List<IDependency> dl : map.values())
			dependencies.addAll(dl);

		return dependencies;
	}

	/**
	 * Return true if the second instruction is dependant on the first.
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

	private static Map<IInstruction,List<IDependency>> getDirDependencies(List<IInstruction>insts, boolean to)
	{
		Map<IInstruction,List<IDependency>> map = new HashMap<IInstruction,List<IDependency>>();
		List<IDependency> dependencies = getGraphNew(insts);
		for(IDependency d : dependencies)
		{
			IInstruction k = to ? d.getTo() : d.getFrom();
			if(!map.containsKey(k))
				map.put(k, new ArrayList<IDependency>());
			List<IDependency> deps = map.get(k);
			deps.add(d);
		}
		return map;
	}

	public static Map<IInstruction,List<IDependency>> getToDependencies(List<IInstruction> insts)
	{
		return getDirDependencies(insts,true);
	}
}
