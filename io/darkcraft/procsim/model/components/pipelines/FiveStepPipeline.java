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

	protected int wbStage = 4;
	protected int memStage = 3;
	protected int exStage  = 2;
	protected int idStage = 1;
	protected int ifStage = 0;

	public String[] stages = new String[]{"IF","ID","EX","MEM","WB"};
	int timer = 0;

	public HashMap<Integer,Pair<IInstruction,Boolean>> pipeline = new HashMap<Integer,Pair<IInstruction,Boolean>>(5);

	protected boolean attemptToMove(int from, Integer to, boolean bReq)
	{
		if(to != null && pipeline.containsKey(to)) return false;
		if(!pipeline.containsKey(from)) return false;
		IInstruction toMove = pipeline.get(from).a;
		if(bReq && !pipeline.get(from).b) return false;
		if(to != null)
		{
			Pair<IInstruction, Boolean> newPair = new Pair(toMove,false);
			pipeline.put(to, newPair);
		}
		pipeline.remove(from);
		return true;
	}

	public void move(int from)
	{
		if(from == wbStage)		attemptToMove(wbStage,	null,		true);
		if(from == memStage)	attemptToMove(memStage,	wbStage,	true);
		if(from == exStage)		attemptToMove(exStage,	memStage,	true);
		if(from == idStage)		attemptToMove(idStage,	exStage,	true);
		if(from == ifStage)		attemptToMove(ifStage,	idStage,	false);
	}

	@Override
	public boolean addInstruction(IInstruction in)
	{
		if(pipeline.containsKey(ifStage))
			return false;
		pipeline.put(ifStage, new Pair<IInstruction, Boolean>(in,false));
		in.start(timer);
		return true;
	}

	public void writeback(AbstractSimulator sim)
	{
		if(pipeline.containsKey(wbStage) && !pipeline.get(wbStage).b)
		{
			IInstruction wb = pipeline.get(wbStage).a;
			wb.finish(timer);
			pipeline.put(wbStage, new Pair(wb,true));
			String out = wb.getOutputRegister();
			if(out != null && !wb.didFail())
				registers.setProperly(wb, out, wb.getOutputRegisterValue());
		}
		move(wbStage);
	}

	public void memory(AbstractSimulator sim)
	{
		if(pipeline.containsKey(memStage) && !pipeline.get(memStage).b)
		{
			IInstruction mem = pipeline.get(memStage).a;
			mem.doMemory(memory);
			Pair<IInstruction,Boolean> newPair = new Pair<IInstruction, Boolean>(mem, true);
			pipeline.put(memStage,newPair);
		}
		if(pipeline.containsKey(memStage) && pipeline.get(memStage).b)
		{
			IInstruction mem = pipeline.get(memStage).a;
			if(memory.doneOperation(mem))
				move(memStage);
		}
	}

	public void execute(AbstractSimulator sim)
	{
		if(pipeline.containsKey(exStage) && !pipeline.get(exStage).b)
		{
			IInstruction exe = pipeline.get(exStage).a;
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
				pipeline.put(exStage,newPair);
			}
		}
		move(exStage);
	}

	public void instructionData(AbstractSimulator sim)
	{
		if(pipeline.containsKey(idStage) && !pipeline.get(idStage).b)
		{
			IInstruction id = pipeline.get(idStage).a;
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
					pipeline.put(idStage,new Pair<IInstruction, Boolean>(id, true));
				}
				else
				{
					System.err.print("");;
				}
			}
		}
		move(idStage);
	}

	public void instructionFetch(AbstractSimulator sim)
	{
		move(ifStage);
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
		IInstruction[] toRet = new IInstruction[getPipelineStages().length];
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

	@Override
	public boolean isFirstExeStage(int stage)
	{
		return stage == exStage;
	}

	private static int[][] exeBlocks = new int[][]{new int[]{2}};
	@Override
	public int[][] getExeBlocks()
	{
		return exeBlocks;
	}

}
