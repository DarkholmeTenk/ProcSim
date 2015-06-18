package io.darkcraft.procsim.model.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapList<A,B>
{
	private Map<A,List<B>> map = new HashMap<A,List<B>>();

	public List<B> getList(A key)
	{
		if(map.containsKey(key))
			return map.get(key);
		List<B> list = new ArrayList<B>();
		map.put(key, list);
		return list;
	}

	public boolean add(A key, B toAdd)
	{
		List<B> list = getList(key);
		return list.add(toAdd);
	}

	public B put(A key, int index, B toPut)
	{
		List<B> list = getList(key);
		return list.set(index, toPut);
	}

	public List<B> put(A key, List<B> toPut)
	{
		return map.put(key, toPut);
	}

	public B get(A key, int index)
	{
		List<B> list = getList(key);
		return list.get(index);
	}

	public Iterator<B> iterator(A key)
	{
		List<B> list = getList(key);
		return list.iterator();
	}

	public Set<A> keySet()
	{
		return map.keySet();
	}

	public Collection<List<B>> values()
	{
		return map.values();
	}

	public List<B> flatten()
	{
		List<B> output = new ArrayList<B>();
		for(List<B> bList : values())
			for(B b : bList)
				output.add(b);
		return output;
	}

	public boolean containsKey(A key)
	{
		return map.containsKey(key);
	}

	public List<B> remove(A key)
	{
		return map.remove(key);
	}

	public int size(A key)
	{
		return getList(key).size();
	}
}
