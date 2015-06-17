package io.darkcraft.procsim.model.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
}
