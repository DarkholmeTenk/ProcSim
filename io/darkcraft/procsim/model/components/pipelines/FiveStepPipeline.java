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
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FiveStepPipeline extends AbstractPipeline
{
	public FiveStepPipeline(IMemory _memory, IRegisterBank _registers, InstructionReader reader)
	{
		super(_memory, _registers, reader);
	}

	public String[] stages = new String[]{"IF","ID","EX","MEM","WB"};
	int timer = 0;

	public HashMap<Integer,Pair<IInstruction,Boolean>> pipeline = new HashMap<Integer,Pair<IInstruction,Boolean>>(5);

	@Override
	public boolean addInstruction(IInstruction in)
	{
		if(pipeline.containsKey(0))
			return false;
		pipeline.put(0, new Pair<IInstruction, Boolean>(in,false));
		in.start(timer);
		return true;
	}

	public void writeback(AbstractSimulator sim)
	{
		if(pipeline.containsKey(4) && !pipeline.get(4).b)
		{
			IInstruction wb = pipeline.get(4).a;
			wb.finish(timer);
			pipeline.put(4, new Pair(wb,true));
			String out = wb.getOutputRegister();
			if(out != null && !wb.didFail())
				registers.setProperly(wb, out, wb.getOutputRegisterValue());
		}
		if(pipeline.containsKey(4) && pipeline.get(4).b)
			pipeline.remove(4);
	}

	public void memory(AbstractSimulator sim)
	{
		if(pipeline.containsKey(3) && !pipeline.get(3).b)
		{
			IInstruction mem = pipeline.get(3).a;
			mem.doMemory(memory);
			Pair<IInstruction,Boolean> newPair = new Pair<IInstruction, Boolean>(mem, true);
			pipeline.put(3,newPair);
		}
		if(pipeline.containsKey(3) && pipeline.get(3).b)
		{
			IInstruction mem = pipeline.get(3).a;
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
	}

	public void execute(AbstractSimulator sim)
	{
		if(pipeline.containsKey(2) && !pipeline.get(2).b)
		{
			IInstruction exe = pipeline.get(2).a;
			Conditional c = exe.getConditional();
			if(c == null) throw new RuntimeException("C is null?");
			Integer status = registers.getStatus(exe);
			if(status != null || c == Conditional.AL)
			{
				if(c == Conditional.AL || c.match(status))
				{
					exe.doExecute();
					String out = exe.getOutputRegister();
					registers.setAvailable(exe, out, exe.getOutputRegisterValue());
					if(exe instanceof Branch)
					{
						int newPC = ((Branch)exe).getAddress(reader);
						registers.setAvailable(exe, "PC", newPC);
						sim.clearAfter(exe.getStartTime());
						sim.flushInstructionCache();
					}
					else if("PC".equals(exe.getOutputRegister()))
					{
						sim.clearAfter(exe.getStartTime());
						sim.flushInstructionCache();
					}
				}
				else
				{
					exe.conditionalFailed();
					registers.remove(exe);
				}
				Pair<IInstruction,Boolean> newPair = new Pair<IInstruction, Boolean>(exe, true);
				pipeline.put(2,newPair);
			}
		}
		if(pipeline.containsKey(2) && pipeline.get(2).b)
		{
			IInstruction exe = pipeline.get(2).a;
			if(!pipeline.containsKey(3))
			{
				pipeline.put(3, new Pair<IInstruction, Boolean>(exe, false));
				pipeline.remove(2);
			}
		}
	}

	public void instructionData(AbstractSimulator sim)
	{
		if(pipeline.containsKey(1) && !pipeline.get(1).b)
		{
			IInstruction id = pipeline.get(1).a;
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
					pipeline.put(1,new Pair<IInstruction, Boolean>(id, true));
				}
				else
				{
					System.err.print("");;
				}
			}
		}
		if(pipeline.containsKey(1) && pipeline.get(1).b)
		{
			IInstruction id = pipeline.get(1).a;
			if(!pipeline.containsKey(2))
			{
				pipeline.put(2,new Pair<IInstruction, Boolean>(id, false));
				pipeline.remove(1);
			}
		}
	}

	public void instructionFetch(AbstractSimulator sim)
	{
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
	public void clearAfter(int spot)
	{
		for(int i = 0; i < stages.length; i++)
		{
			if(pipeline.containsKey(i))
			{
				if(pipeline.get(i).a.getStartTime() > spot)
				{
					pipeline.get(i).a.leaveEarly();
					registers.remove(pipeline.get(i).a);
					pipeline.remove(i);
				}
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

	@Override
	public List<String> getDangerousOut()
	{
		ArrayList<String> toRet = new ArrayList<String>();
		for(int i = 0; i <= 1; i++)
		{
			if(!pipeline.containsKey(i)) continue;
			if(pipeline.get(i).b) continue;
			IInstruction inst = pipeline.get(i).a;
			if(!pipeline.get(i).b)
				toRet.add(inst.getOutputRegister());
		}
		while(toRet.contains(null))
			toRet.remove(null);
		return toRet;
	}


	@Override
	public List<String> getDangerousIn()
	{
		ArrayList<String> toRet = new ArrayList<String>();
		for(int i = 0; i <= 1; i++)
		{
			if(!pipeline.containsKey(i)) continue;
			if(pipeline.get(i).b) continue;
			IInstruction inst = pipeline.get(i).a;
			String[] input = inst.getInputRegisters();
			if(input != null)
				for(String s : input)
					toRet.add(s);
		}
		while(toRet.contains(null))
			toRet.remove(null);
		return toRet;
	}

	@Override
	public int getInstructionTime(int stage)
	{
		if(pipeline.containsKey(stage))
			return pipeline.get(stage).a.getStartTime();
		return -1;
	}

	@Override
	public void increaseTimer()
	{
		timer++;
	}

	@Override
	public void stepStage(AbstractSimulator sim, int stage)
	{
		switch(stage)
		{
		case 4: writeback(sim); break;
		case 3: memory(sim); break;
		case 2: execute(sim); break;
		case 1: instructionData(sim); break;
		case 0: instructionFetch(sim); break;
		}
	}

	@Override
	public IInstruction getInstruction(int stage)
	{
		Pair<IInstruction,Boolean> p = pipeline.get(stage);
		if(p != null)
			return p.a;
		return null;
	}

	@Override
	public int getLastIDStage()
	{
		return 1;
	}

}
