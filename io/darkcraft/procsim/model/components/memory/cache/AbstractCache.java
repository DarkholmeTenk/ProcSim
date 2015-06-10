package io.darkcraft.procsim.model.components.memory.cache;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.helper.OutputHelper;

public abstract class AbstractCache implements IMemory
{
	public int cacheLevel;
	protected final IMemory nextLevel;
	AbstractCache(int level, IMemory nl)
	{
		nextLevel = nl;
		setLevel(level);
	}

	public void setLevel(int i)
	{
		cacheLevel = i;
	}

	public void incLevel()
	{
		if(nextLevel instanceof AbstractCache)
			((AbstractCache)nextLevel).incLevel();
		setLevel(cacheLevel+1);
	}

	@Override
	public String toString()
	{
		return nextLevel.toString() + " < L"+cacheLevel + " " + getName() +"("+OutputHelper.byteString(getSize())+")";
	}

	@Override
	public void read()
	{
		nextLevel.read();
	}

	public abstract String getName();
}
