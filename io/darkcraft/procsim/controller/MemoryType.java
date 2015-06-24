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

	public static MemoryType getMT(IMemory m)
	{
		if(m instanceof StandardMemory) return STANDARD;
		if(m instanceof DMCache) return DMC;
		return null;
	}

	public static String getString(IMemory m)
	{
		if(m == null) return "";
		MemoryType t = getMT(m);
		if(m instanceof AbstractCache)
		{
			AbstractCache c = (AbstractCache)m;
			return t.name +","+c.cacheLineSize+","+c.size+ ","+c.cacheLevel +"|"+ getString(c.nextLevel);
		}
		return t.name + "," + m.getWordSize()+ "," + m.getSize();
	}

	private static int toInt(String s)
	{
		return Integer.parseInt(s);
	}

	public static IMemory getMem(String s, File f)
	{
		IMemory m = null;
		String[] memData = s.split("\\|");
		for(int i = memData.length-1; i>= 0; i--)
		{
			String[] data = memData[i].split(",");
			MemoryType t = get(data[0]);
			m = t.getMemory(toInt(data[2]),toInt(data[1]),m,f);
			if(m instanceof AbstractCache)
				((AbstractCache) m).setLevel(toInt(data[3]));
		}
		return m;
	}
}
