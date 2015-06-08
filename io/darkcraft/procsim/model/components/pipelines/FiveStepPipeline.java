package io.darkcraft.procsim.model.components.pipelines;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.components.registerbank.Register;
import io.darkcraft.procsim.model.helper.Pair;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.instruction.instructions.Branch;

import java.util.HashMap;

public class FiveStepPipeline extends AbstractPipeline
{
	public FiveStepPipeline(IMemory _memory, IRegisterBank _registers, InstructionReader reader)
	{
		super(_memory, _registers, reader);
	}

	public String[] stages = new String[]{"IF","ID","EX","MEM","WB"};

	public HashMap<Integer,Pair<IInstruction,Boolean>> pipeline = new HashMap<Integer,Pair<IInstruction,Boolean>>(5);

	@Override
	public boolean addInstruction(IInstruction in)
	{
		if(pipeline.containsKey(0))
			return false;
		pipeline.put(0, new Pair<IInstruction, Boolean>(in,false));
		return true;
	}

	@Override
	public void step()
	{
		//Write back
		if(pipeline.containsKey(4))
		{
			IInstruction wb = pipeline.get(4).a;
			String out = wb.getOutputRegister();
			if(out != null)
				registers.setProperly(wb, out, wb.getOutputRegisterValue());
			pipeline.remove(4);
		}

		//Memory
		if(pipeline.containsKey(3))
		{
			IInstruction mem = pipeline.get(3).a;
			if(!pipeline.get(3).b)
			{
				mem.doMemory(memory);
				Pair<IInstruction,Boolean> newPair = new Pair<IInstruction, Boolean>(mem, true);
				pipeline.put(3,newPair);
			}

			if(memory.doneOperation(mem))
			{
				if(!pipeline.containsKey(4))
				{
					Pair<IInstruction,Boolean> newPair = new Pair<IInstruction, Boolean>(mem, false);
					pipeline.put(4, newPair);
					pipeline.remove(3);
				}
			}
		}

		//Execute
		if(pipeline.containsKey(2))
		{
			IInstruction exe = pipeline.get(2).a;
			Conditional c = exe.getConditional();
			if(c == null) throw new RuntimeException("C is null?");
			Integer status = registers.getStatus(exe);
			boolean rem = false;
			if((!pipeline.get(2).b) && (status != null || c == Conditional.AL))
			{
				if(c.match(status))
				{
					exe.doExecute();
					String out = exe.getOutputRegister();
					registers.setAvailable(exe, out, exe.getOutputRegisterValue());
					if(exe instanceof Branch)
					{
						int newPC = ((Branch)exe).getAddress(reader);
						registers.setAvailable(exe, "PC", newPC);
						clearUpTo(2);
					}
				}
				else
					rem = true;
				Pair<IInstruction,Boolean> newPair = new Pair<IInstruction, Boolean>(exe, true);
				pipeline.put(2,newPair);
			}

			if(pipeline.get(2).b && (!pipeline.containsKey(3)))
			{
				if(!rem)
					pipeline.put(3, new Pair<IInstruction, Boolean>(exe, false));
				pipeline.remove(2);
				if(rem)
					registers.remove(exe);
			}
		}

		//Instruction data
		if(pipeline.containsKey(1))
		{
			IInstruction id = pipeline.get(1).a;
			if(!pipeline.get(1).b)
			{
				String[] inputRegisters = id.getInputRegisters();
				int[] vals = new int[inputRegisters == null ? 0 : inputRegisters.length];
				boolean allReady = true;
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
					String o = id.getOutputRegister();
					if(o != null)
					{
						Register r = registers.get(o);
						r.lock(id);
					}
					pipeline.put(1,new Pair<IInstruction, Boolean>(id, true));
				}
			}
			if(pipeline.get(1).b && (!pipeline.containsKey(2)))
			{
				pipeline.put(2,new Pair<IInstruction, Boolean>(id, false));
				pipeline.remove(1);
			}
		}

		//Instruction fetch
		if(pipeline.containsKey(0))
		{
			if(!pipeline.containsKey(1))
			{
				pipeline.put(1,pipeline.get(0));
				pipeline.remove(0);
			}
		}
	}

	@Override
	public String[] getPipelineStages()
	{
		return stages;
	}

	@Override
	public String[] getPipelineShorts()
	{
		return stages;
	}

	@Override
	public boolean isEmpty()
	{
		return pipeline.size() == 0;
	}

	@Override
	public void clearUpTo(int spot)
	{
		for(int i = 0; i < spot; i++)
			pipeline.remove(i);
		registers.unlockAll();
		for(int i = spot; i < stages.length; i++)
		{
			if(pipeline.get(i) != null)
			{
				IInstruction ins = pipeline.get(i).a;
				String out = ins.getOutputRegister();
				if(out != null)
					registers.get(out).lock(ins);
			}
		}
	}

	@Override
	public void clear()
	{
		for(int i = 0; i < pipeline.size(); i++)
			pipeline.remove(i);
		registers.unlockAll();
	}

	@Override
	public Integer getPosition(IInstruction inst)
	{
		for(Integer i : pipeline.keySet())
		{
			if(pipeline.get(i).equals(inst))
				return i;
		}
		return null;
	}

	@Override
	public IInstruction[] getState()
	{
		IInstruction[] toRet = new IInstruction[stages.length];
		for(int i = 0; i < toRet.length; i++)
		{
			if(pipeline.containsKey(i))
			{
				IInstruction inst = pipeline.get(i).a;
				toRet[i] = inst;
			}
			else
				toRet[i] = null;
		}
		return toRet;
	}

}
