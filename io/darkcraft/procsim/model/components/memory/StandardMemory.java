package io.darkcraft.procsim.model.components.memory;

import io.darkcraft.procsim.controller.MemoryState;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.helper.OutputHelper;
import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.IMemoryInstruction;
import io.darkcraft.procsim.model.instruction.MemoryInstructionType;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class StandardMemory implements IMemory
{
	public final static int					WORDSIZE	= 1;
	public final static int					READTIME	= 50;
	public final static int					WRITETIME	= 51;

	private int								reads		= 0;
	private int								writes		= 0;
	private final int						size;
	private final int[]						data;
	private final HashMap<String, Integer>	mnemonicMap	= new HashMap<String, Integer>();
	private int								timer		= 0;
	private HashMap<Object, Integer>		inTimes		= new HashMap<Object, Integer>();
	private File							inputData;

	public StandardMemory(int _size, File _inputData)
	{
		size = _size;
		data = new int[size];
		inputData = _inputData;
	}

	@Override
	public int getSize()
	{
		return size;
	}

	@Override
	public int getWordSize()
	{
		return WORDSIZE;
	}

	@Override
	public int getValue(Object i, int location)
	{
		if (i != null)
		{
			reads++;
			inTimes.put(i, timer);
		}
		return data[location];
	}

	@Override
	public void setValue(Object i, int location, int value)
	{
		if (i != null)
			writes++;
		inTimes.put(i, timer);
		data[location] = value;
	}

	@Override
	public int getLocation(String identifier)
	{
		if (mnemonicMap.containsKey(identifier))
			return mnemonicMap.get(identifier);
		return 0;
	}

	@Override
	public boolean doneOperation(Object i)
	{
		if (!inTimes.containsKey(i))
			return true;
		int inTime = inTimes.get(i);
		if (i instanceof IMemoryInstruction)
		{
			MemoryInstructionType type = ((IMemoryInstruction) i).getType();
			if (type == MemoryInstructionType.READ)
				return timer >= (inTime + READTIME);
			else
				return timer >= (inTime + WRITETIME);
		}
		return timer >= (inTime + READTIME);
	}

	@Override
	public void step(AbstractSimulator sim)
	{
		timer++;
	}

	@Override
	public String toString()
	{
		return "Main Memory (" + OutputHelper.byteString(size * getWordSize()) + ")";
	}

	@Override
	public void read()
	{
		if (inputData == null || (inputData.isDirectory() || !inputData.exists()))
			return;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(inputData));
			String line = null;
			int i = 0;
			while ((line = reader.readLine()) != null)
			{
				if (line.isEmpty())
					continue;
				String[] split = line.split(ReadingHelper.splitRegex, 2);
				if (split.length == 2)
				{
					mnemonicMap.put(split[0], i);
					data[i++] = Integer.parseInt(split[1], 16);
				}
				else
					data[i++] = Integer.parseInt(split[0], 16);
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setFile(File newFile)
	{
		inputData = newFile;
	}

	@Override
	public IMemory clone()
	{
		return new StandardMemory(size, inputData);
	}

	@Override
	public IMemory[] getStack()
	{
		return new IMemory[]
		{ this };
	}

	@Override
	public MemoryState getState()
	{
		return new MemoryState(this, reads, writes);
	}
}
