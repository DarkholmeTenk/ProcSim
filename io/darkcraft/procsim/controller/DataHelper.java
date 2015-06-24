package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.instruction.IInstruction;

import java.util.HashSet;
import java.util.List;

public class DataHelper
{

	public static boolean equal(IInstruction[][] a, IInstruction[][] b)
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

	public static HashSet<Integer> getDuplicateStates(List<IInstruction[][]> states)
	{
		HashSet<Integer> dupes = new HashSet<Integer>();
		for(int i = 1; i < states.size() - 1; i++)
		{
			IInstruction[][] prev = states.get(i - 1);
			IInstruction[][] curr = states.get(i);
			if(equal(prev, curr))
			{
				IInstruction[][] next = states.get(i + 1);
				if(equal(curr, next))
					dupes.add(i);
			}

		}
		return dupes;
	}
}
