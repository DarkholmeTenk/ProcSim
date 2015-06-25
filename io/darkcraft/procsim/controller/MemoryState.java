package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.components.abstracts.IMemory;

public class MemoryState
{
	public final int			conflicts;
	public final int			misses;
	public final int			reads;
	public final int			writes;
	public final IMemory		mem;
	public final MemoryState	nextLevel;

	public MemoryState(IMemory me, int r, int w)
	{
		this(me, r, w, 0, 0, null);
	}

	public MemoryState(IMemory me, int r, int w, int c, int m)
	{
		this(me, r, w, c, m, null);
	}

	public MemoryState(IMemory me, int r, int w, int c, int m, MemoryState n)
	{
		mem = me;
		reads = r;
		writes = w;
		conflicts = c;
		misses = m;
		nextLevel = n;
	}
}
