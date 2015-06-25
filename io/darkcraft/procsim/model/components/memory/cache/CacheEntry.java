package io.darkcraft.procsim.model.components.memory.cache;

import io.darkcraft.procsim.model.components.abstracts.IMemory;

public class CacheEntry
{
	private int[]			data	= null;
	private int				oldAddress;
	private int				tag		= -1;
	private final int		tagSize;
	private final int		tagBlock;
	private final DMCache	owner;
	private boolean			dirty = false;

	public CacheEntry(int _tagSize, int cacheLineSize, int _memSize, DMCache _owner)
	{
		tagSize = _tagSize;
		data = new int[cacheLineSize];
		for (int i = 0; i < data.length; i++)
			data[i] = 0;
		tagBlock = (int) Math.floor(Math.pow(2, tagSize));
		owner = _owner;
	}

	public int getTag()
	{
		return tag;
	}

	private int[] split(int address)
	{
		int tTag = address / tagBlock;
		int offset = address % data.length;
		return new int[]
		{ tTag, offset };
	}

	private int addressBase(int address)
	{
		return data.length * (address / data.length);
	}

	private void fill(int address, IMemory mem)
	{
		int[] split = split(address);
		if(dirty == true)
			owner.nextLevel.setValue(this, oldAddress, data[split[1]]);
		int base = addressBase(address);
		if(tag == -1)
			owner.miss();
		else
			owner.conflict();
		tag = split[0];

		for(int i = 0; i < data.length; i++)
			data[i] = mem.getValue(this, base + i);
		oldAddress = address;
	}

	public Integer get(int address, IMemory mem)
	{
		int[] split = split(address);
		if (split[0] != tag)
			fill(address, mem);
		return data[split[1]];
	}

	public void set(int address, IMemory mem, int value)
	{
		int[] split = split(address);
		if (split[0] != tag)
			fill(address, mem);
		data[split[1]] = value;
		dirty = true;
	}

}
