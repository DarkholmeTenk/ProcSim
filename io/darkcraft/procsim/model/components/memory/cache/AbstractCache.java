package io.darkcraft.procsim.model.components.memory.cache;

import io.darkcraft.procsim.model.components.abstracts.IMemory;

/**
 * An abstract representation of a cache
 *
 * @author mbax2sb4
 */
public abstract class AbstractCache implements IMemory
{
	public int				cacheLevel;
	public final IMemory	nextLevel;
	public final int		size;
	public final int		cacheLineSize;
	protected int			reads;
	protected int			writes;

	AbstractCache(int level, IMemory nl, int _size, int _cacheLineSize)
	{
		nextLevel = nl;
		size = _size;
		cacheLineSize = _cacheLineSize;
		setLevel(level);
	}

	/**
	 * Sets the level of the cache to i.
	 * Used to set the read/write times to a time appropriate for a different level of cache.
	 *
	 * @param i
	 */
	public void setLevel(int i)
	{
		cacheLevel = i;
		if (nextLevel instanceof AbstractCache)
		{
			AbstractCache nextCache = (AbstractCache) nextLevel;
			nextCache.setLevel(i + 1);
		}
	}

	/**
	 * Increment the level of cache
	 */
	public void incLevel()
	{
		setLevel(cacheLevel + 1);
	}

	@Override
	public IMemory[] getStack()
	{
		int stackSize = 2;
		IMemory next = nextLevel;
		while (next instanceof AbstractCache)
		{
			next = ((AbstractCache) next).nextLevel;
			stackSize++;
		}
		IMemory[] stack = new IMemory[stackSize];
		stack[--stackSize] = this;
		next = nextLevel;
		while (next instanceof AbstractCache)
		{
			stack[--stackSize] = next;
			next = ((AbstractCache) next).nextLevel;
		}
		stack[--stackSize] = next;
		return stack;
	}

	@Override
	public void read()
	{
		nextLevel.read();
	}

	/**
	 * @return a string representing the type of cache this is (e.g. DMC for Direct Mapped Cache)
	 */
	public abstract String getName();

	// Clone is a reserved function because of reasons :(
	@Override
	public abstract IMemory clone();

	/**
	 * Clones memory further up, but replaces toReplace with newOne when it is encountered
	 *
	 * @param toReplace
	 *            the memory to replace
	 * @param newOne
	 *            the memory to insert in place of toReplace
	 * @return
	 */
	public abstract IMemory cloneUp(IMemory toReplace, IMemory newOne);

	@Override
	public String toString()
	{
		return "L" + cacheLevel + " " + getName() + "(" + size + " rows @" + cacheLineSize + " words)";
	}
}
