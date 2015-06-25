package io.darkcraft.procsim.model.components.memory.cache;

import io.darkcraft.procsim.controller.MemoryState;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.instruction.IMemoryInstruction;
import io.darkcraft.procsim.model.instruction.MemoryInstructionType;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.io.File;
import java.util.HashMap;

public class DMCache extends AbstractCache
{

	private final CacheEntry[]				data;
	private HashMap<Object, Integer>		inTimes		= new HashMap<Object, Integer>();
	private HashMap<Object, Integer>		inLocs		= new HashMap<Object, Integer>();
	private int								timer		= 0;
	private int								conflicts	= 0;
	private int								misses		= 0;
	public final static int					READTIMEL1	= 0;
	public final static int					WRITETIMEL1	= 0;
	public final static int					READTIMEL2	= 2;
	public final static int					WRITETIMEL2	= 4;
	public final static int					READTIMEL3	= 6;
	public final static int					WRITETIMEL3	= 8;
	private int						readTime;
	private int						writeTime;

	public DMCache(int size, int cacheLineSize, IMemory memory, int level)
	{
		super(level,memory, size, cacheLineSize);
		data = new CacheEntry[size];
		int memSize = memory.getSize();
		int tagSize = (int) Math.ceil(Math.log(size) / Math.log(2));
		for (int i = 0; i < size; i++)
			data[i] = new CacheEntry(tagSize, cacheLineSize, memSize, this);
	}

	@Override
	public void setLevel(int level)
	{
		super.setLevel(level);
		if(level <= 1)
		{
			readTime = READTIMEL1;
			writeTime = WRITETIMEL1;
		}
		else if(level == 2)
		{
			readTime = READTIMEL2;
			writeTime = WRITETIMEL2;
		}
		else
		{
			readTime = READTIMEL3;
			writeTime = WRITETIMEL3;
		}
	}

	public void conflict()
	{
		conflicts++;
	}

	public void miss()
	{
		misses++;
	}

	@Override
	public int getSize()
	{
		return size * cacheLineSize * getWordSize();
	}

	@Override
	public int getWordSize()
	{
		return nextLevel.getWordSize();
	}

	private CacheEntry getCell(int address)
	{
		return data[(address % getSize()) / cacheLineSize];
	}

	@Override
	public int getValue(Object i, int location)
	{
		if(i != null)
			reads++;
		CacheEntry ent = getCell(location);
		inTimes.put(i, timer);
		inLocs.put(i, location);
		return ent.get(location, nextLevel);
	}

	@Override
	public void setValue(Object i, int location, int value)
	{
		if(i != null)
			writes++;
		CacheEntry ent = getCell(location);
		inTimes.put(i, timer);
		inLocs.put(i, location);
		ent.set(location, nextLevel, value);
	}

	@Override
	public int getLocation(String identifier)
	{
		return nextLevel.getLocation(identifier);
	}

	@Override
	public boolean doneOperation(Object i)
	{
		if(!inTimes.containsKey(i)) return true;
		int loc = inLocs.get(i);
		if(!nextLevel.doneOperation(getCell(loc))) return false;
		int inTime = inTimes.get(i);
		if(i instanceof IMemoryInstruction)
		{
			MemoryInstructionType type = ((IMemoryInstruction)i).getType();
			if(type == MemoryInstructionType.READ)
				return timer >= (inTime + readTime);
			else
				return timer >= (inTime + writeTime);
		}
		return timer >= (inTime + writeTime);
	}

	@Override
	public void step(AbstractSimulator sim)
	{
		timer++;
		nextLevel.step(sim);
	}

	@Override
	public String getName()
	{
		return "DMC";
	}

	@Override
	public void setFile(File newFile)
	{
		nextLevel.setFile(newFile);
	}

	@Override
	public IMemory clone()
	{
		IMemory next = nextLevel.clone();
		return new DMCache(size, cacheLineSize, next, cacheLevel);
	}

	@Override
	public IMemory cloneUp(IMemory toReplace, IMemory newOne)
	{
		if(nextLevel == toReplace)
			return new DMCache(size,cacheLineSize,newOne,cacheLevel);
		else if(nextLevel instanceof AbstractCache)
			return new DMCache(size,cacheLineSize,((AbstractCache)nextLevel).cloneUp(toReplace,newOne),cacheLevel);
		else
			return new DMCache(size,cacheLineSize,nextLevel.clone(),cacheLevel);
	}

	@Override
	public MemoryState getState()
	{
		return new MemoryState(this,reads,writes,conflicts,misses,nextLevel.getState());
	}
}
