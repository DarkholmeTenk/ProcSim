package io.darkcraft.procsim.model.instruction;

import io.darkcraft.procsim.model.error.InvalidInstructionException;
import io.darkcraft.procsim.model.helper.ReadingHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructionReader
{
	private final File		file;
	private BufferedReader	reader	= null;
	private IInstruction	next	= null;

	public InstructionReader(File f)
	{
		file = f;
	}

	public void open()
	{
		try
		{
			reader = new BufferedReader(new FileReader(file));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void close()
	{
		try
		{
			reader.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private IInstruction getNext()
	{
		try
		{
			String line = reader.readLine();
			if(line == null) return null;
			while(line != null && line.isEmpty()) line = reader.readLine();
			if(line == null) return null;
			IInstruction inst = InstructionFactory.get(line);
			String mnem = null;
			if(inst == null)
			{
				String[] data = line.split(ReadingHelper.splitRegex,2);
				mnem = data[0];
				inst = InstructionFactory.get(data[1]);
			}
			if(inst == null)
				throw new InvalidInstructionException(line);
			store.add(inst);
			if(mnem != null)
			{
				int slot = store.size() -1;
				mnemonicMap.put(mnem, slot);
			}
			return inst;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public boolean hasNext()
	{
		if(next == null)
			next = getNext();
		return next != null;
	}

	public IInstruction next()
	{
		if(hasNext())
		{
			IInstruction n = next;
			next = null;
			return n;
		}
		return null;
	}

	private HashMap<String,Integer> mnemonicMap = new HashMap<String,Integer>();
	private ArrayList<IInstruction> store = new ArrayList<IInstruction>();

	public IInstruction get(int address)
	{
		int slot = address >> 2;
		if(slot >= store.size())
		{
			while(next() != null && slot >= store.size());
			if(slot > store.size())
				throw new InvalidInstructionException("Instruction at " + slot + " is not valid!");
			else if(slot == store.size())
				return null;
		}
		return store.get(slot);
	}

	public int get(String mnemonic)
	{
		while(!mnemonicMap.containsKey(mnemonic) && (next() != null));
		if(!mnemonicMap.containsKey(mnemonic)) throw new InvalidInstructionException("Invalid mnemonic " + mnemonic);
		int slot = mnemonicMap.get(mnemonic);
		return slot;
	}

	public List<IInstruction> getAll()
	{
		return store;
	}
}
