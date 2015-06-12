package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.dependencies.RAW;
import io.darkcraft.procsim.model.dependencies.WAR;
import io.darkcraft.procsim.model.dependencies.WAW;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.instructions.Branch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyGraphBuilder
{
	private static HashMap<IInstruction,List<IDependency>> buildFromMap(List<IDependency> dependencies)
	{
		HashMap<IInstruction,List<IDependency>> fromMap = new HashMap<IInstruction,List<IDependency>>();
		for(IDependency dependency : dependencies)
		{
			IInstruction from = dependency.getFrom();
			if(!fromMap.containsKey(from))
				fromMap.put(from, new ArrayList<IDependency>());
			List<IDependency> list = fromMap.get(from);
			list.add(dependency);
		}
		return fromMap;
	}

	private static int linkExists(HashMap<IInstruction,List<IDependency>> fromMap, IInstruction from, IInstruction to, String type)
	{
		List<IDependency> dependencies = fromMap.get(from);
		if(dependencies == null || dependencies.size() == 0) return -1;
		for(int i = 0; i < dependencies.size(); i++)
		{
			IDependency dep = dependencies.get(i);
			if(dep.getTo().equals(to) && dep.getType().equals(type))
				return i;
		}
		return -1;
	}

	private static List<IDependency> removeTransitives(List<IDependency> dependencies)
	{
		HashMap<IInstruction,List<IDependency>> fromMap = buildFromMap(dependencies);
		for(IInstruction inst : fromMap.keySet())
		{
			List<IDependency> deps = fromMap.get(inst);
			for(IDependency a : deps)
			{
				for(IDependency b : deps)
				{
					if(a.equals(b)) continue;
					if(!a.getType().equals(b.getType())) continue;
					int link = linkExists(fromMap,a.getTo(),b.getTo(),a.getType());
					if(link != -1)
						dependencies.remove(b);
				}
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
		dependencies = removeTransitives(dependencies);

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
		List<IDependency> dependencies = getGraph(insts);
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
