package io.darkcraft.procsim.model.components.memory.cache;

import io.darkcraft.procsim.model.components.abstracts.IMemory;

public abstract class AbstractCache implements IMemory
{
	public int cacheLevel;
	public final IMemory nextLevel;
	public final int						size;
	public final int						cacheLineSize;
	AbstractCache(int level, IMemory nl, int _size, int _cacheLineSize)
	{
		nextLevel = nl;
		size = _size;
		cacheLineSize = _cacheLineSize;
		setLevel(level);
	}

	public void setLevel(int i)
	{
		cacheLevel = i;
		if(nextLevel instanceof AbstractCache)
		{
			AbstractCache nextCache = (AbstractCache) nextLevel;
			nextCache.setLevel(i+1);
		}
	}

	public void incLevel()
	{
		setLevel(cacheLevel+1);
	}

	@Override
	public String toString()
	{
		return "L"+cacheLevel + " " + getName() +"("+size + " rows @" + cacheLineSize + " words)";
	}

	@Override
	public IMemory[] getStack()
	{
		int stackSize = 2;
		IMemory next = nextLevel;
		while(next instanceof AbstractCache)
		{
			next = ((AbstractCache)next).nextLevel;
			stackSize++;
		}
		IMemory[] stack = new IMemory[stackSize];
		stack[--stackSize] = this;
		next = nextLevel;
		while(next instanceof AbstractCache)
		{
			stack[--stackSize] = next;
			next = ((AbstractCache)next).nextLevel;
		}
		stack[--stackSize] = next;
		return stack;
	}

	@Override
	public void read()
	{
		nextLevel.read();
	}

	public abstract String getName();

	@Override
	public abstract IMemory clone();

	public abstract IMemory cloneUp(IMemory toReplace, IMemory newOne);
}
