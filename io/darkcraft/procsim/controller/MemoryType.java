package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.memory.StandardMemory;
import io.darkcraft.procsim.model.components.memory.cache.AbstractCache;
import io.darkcraft.procsim.model.components.memory.cache.DMCache;

import java.io.File;

public enum MemoryType
{
	STANDARD("Main Memory",false),
	DMC("Direct Mapped Cache",true);

	public final String name;
	public final boolean requiresNextLevel;

	private MemoryType(String n, boolean requireNL)
	{
		name = n;
		requiresNextLevel = requireNL;
	}

	public IMemory getMemory(int size, int cacheLineSize, IMemory nextLevel, File data)
	{
		if(requiresNextLevel && nextLevel == null) throw new RuntimeException("No upper level of memory");
		if(this == STANDARD) return new StandardMemory(size,data);
		if(nextLevel instanceof AbstractCache)
			((AbstractCache)nextLevel).incLevel();
		if(this == DMC)
			return new DMCache(size, cacheLineSize, nextLevel, 1);
		return null;
	}

	public String getName()
	{
		return name;
	}

	public static MemoryType get(String name)
	{
		for(MemoryType s : values())
			if(s.getName().equals(name))
				return s;
		return null;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
