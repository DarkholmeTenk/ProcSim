package io.darkcraft.procsim.model.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class which maps a key A to a list of B values
 * @author Shane Booth
 *
 * @param <A> the class of key to be used
 * @param <B> the class of value to be used
 */
public class MapList<A,B>
{
	private Map<A,List<B>> map = new HashMap<A,List<B>>();

	/**
	 * @param key
	 * @return the list of values referred to by key.
	 */
	public List<B> getList(A key)
	{
		if(map.containsKey(key))
			return map.get(key);
		List<B> list = new ArrayList<B>();
		map.put(key, list);
		return list;
	}

	/**
	 * Adds the value toAdd to the list of values represented by key.
	 * @param key the key to use
	 * @param toAdd the value to be added
	 * @return true as an add cannot fail
	 */
	public boolean add(A key, B toAdd)
	{
		List<B> list = getList(key);
		return list.add(toAdd);
	}

	/**
	 * Adds toPut into the list represented by key at slot index
	 * @param key the key value to identify which list
	 * @param index the slot at which toPut should be inserted
	 * @param toPut the value to be inserted
	 * @return the element previously at the specified position
	 *
	 */
	public B put(A key, int index, B toPut)
	{
		List<B> list = getList(key);
		return list.set(index, toPut);
	}

	/**
	 * Replaces the list specified by key with the list toPut
	 * @param key the key of the list to replace
	 * @param toPut the new list to be put in
	 * @return the previous value represented by key
	 */
	public List<B> put(A key, List<B> toPut)
	{
		return map.put(key, toPut);
	}

	/**
	 * Returns the value stored in list represented by key and at the position index.
	 * @param key the key representing the list
	 * @param index the index in the list to retrieve the value from
	 * @return the value held in list A at index
	 */
	public B get(A key, int index)
	{
		List<B> list = getList(key);
		return list.get(index);
	}

	/**
	 * Returns an iterator over the list represented by key
	 * @param key the key to identify which list to return the iterator of
	 * @return an iterator representing the list identified by key
	 */
	public Iterator<B> iterator(A key)
	{
		List<B> list = getList(key);
		return list.iterator();
	}

	/**
	 * @return the set of keys in the map
	 */
	public Set<A> keySet()
	{
		return map.keySet();
	}

	/**
	 * @return The collection of all the lists in the map
	 */
	public Collection<List<B>> values()
	{
		return map.values();
	}

	/**
	 * @return a list which represents all of the values in all of the lists in the map.
	 */
	public List<B> flatten()
	{
		List<B> output = new ArrayList<B>();
		for(List<B> bList : values())
			for(B b : bList)
				output.add(b);
		return output;
	}

	/**
	 * @param key
	 * @return true if the map contains key. False otherwise.
	 */
	public boolean containsKey(A key)
	{
		return map.containsKey(key);
	}

	/**
	 * Removes the list idenfitied by key.
	 * @param key
	 * @return the list previously identified by key
	 */
	public List<B> remove(A key)
	{
		return map.remove(key);
	}

	/**
	 * @param key
	 * @return the number of items in the list identified by key
	 */
	public int size(A key)
	{
		return getList(key).size();
	}
}
