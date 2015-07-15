package io.darkcraft.procsim.model.components.pipelines;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.registerbank.Register;
import io.darkcraft.procsim.model.helper.Pair;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

public class OneFunctionalUnitOOOPipeline extends FiveStepPipeline
{

	public OneFunctionalUnitOOOPipeline(IMemory _memory, IRegisterBank _registers, InstructionReader reader)
	{
		super(_memory, _registers, reader);
		stages = new String[]{"EX","MEM","WB"};
		ifStage = -1;
		idStage = -1;
		exStage = 0;
		memStage = 1;
		wbStage = 2;
	}

	private boolean clearToAdd(IInstruction id)
	{
		String[] inputRegisters = id.getInputRegisters();
		int[] vals = new int[inputRegisters == null ? 0 : inputRegisters.length];
		boolean allReady = true;
		String o = id.getOutputRegister();
		if(o != null)
		{
			Register r = registers.get(o);
			if(r != null)
				allReady = r.lock(id);
			else
				throw new RuntimeException("No register named : " + o);

		}
		if(allReady)
		{
			if(vals.length > 0)
			{
				for(int i = 0; i < vals.length; i++)
				{
					String reg = inputRegisters[i];
					if(reg == null) continue;
					Integer val = registers.getValue(reg, id);
					if(val == null)
					{
						allReady = false;
						break;
					}
					vals[i] = val;
				}
			}
			if(allReady)
			{
				id.setInputRegisters(vals);
				return true;
				//pipeline.put(idStage,new Pair<IInstruction, Boolean>(id, true));
			}
			else
			{
				System.err.print("");;
			}
		}
		return false;
	}

	@Override
	public boolean addInstruction(IInstruction in)
	{
		if(pipeline.containsKey(exStage))
			return false;
		if(clearToAdd(in))
		{
			pipeline.put(exStage, new Pair<IInstruction, Boolean>(in,false));
			in.start(timer);
			return true;
		}
		return false;
	}

	@Override
	public void stepStage(AbstractSimulator sim, int stage)
	{
		switch(stage)
		{
		case 2: writeback(sim); break;
		case 1: memory(sim); break;
		case 0: execute(sim); break;
		}
	}

}
