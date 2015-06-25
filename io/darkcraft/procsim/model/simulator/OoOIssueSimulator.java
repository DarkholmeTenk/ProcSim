package io.darkcraft.procsim.model.simulator;

import io.darkcraft.procsim.controller.DependencyGraphBuilder;
import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;
import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.components.abstracts.IRegisterBank;
import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionReader;
import io.darkcraft.procsim.model.instruction.instructions.Branch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OoOIssueSimulator extends OoOExSimulator
{
	private static final int maxSize = 16;
	private List<IInstruction> window = new ArrayList(maxSize);

	public OoOIssueSimulator(IMemory _mem, IRegisterBank _reg, InstructionReader _reader, AbstractPipeline... _pipelines)
	{
		super(_mem, _reg, _reader, _pipelines);
	}

	@Override
	public void flushInstructionCache(int id)
	{
		if(window.isEmpty()) return;
		for(Iterator<IInstruction> iter = window.iterator(); iter.hasNext();)
		{
			IInstruction i = iter.next();
			if(i.getAddress() > id)
				iter.remove();
		}
	}

	private void nextInstructionsToWindow()
	{
		for(int i = 0; i < pipeline.length*2 && window.size() < maxSize; i++)
		{
			int pc = reg.getValue("PC", null);
			IInstruction inst = reader.get(pc);
			if(inst != null)
			{
				window.add(inst);
				reg.incrementPC();
			}
		}
	}

	private void assignFromWindow()
	{
		mainLoop:
		for(int counter = 0; counter < pipeline.length; counter++)
		{
			instructionLoop:
			for(int i = 0; i < window.size(); i++)
			{
				IInstruction inst = window.get(i);
				//Check it doesn't conflict with anything earlier in the list
				for(int j = 0; j < i; j++)
				{
					IInstruction to = window.get(j);
					if(to instanceof Branch || (to != null &&
												to.getOutputRegister() != null && to.getOutputRegister().equals("PC")))
						continue instructionLoop;
					List<IDependency> deps = DependencyGraphBuilder.getDependencies(inst, to);
					if(deps.size() > 0)
						continue instructionLoop;
				}

				//Check pipeline stages which haven't had registers locked
				for(AbstractPipeline pl : pipeline)
					for(int j = 0; j <= pl.getLastIDStage(); j++)
					{
						IInstruction to = pl.getInstruction(j);
						if(to == null) continue;
						List<IDependency> deps = DependencyGraphBuilder.getDependencies(inst, to);
						if(deps.size() > 0)
							continue instructionLoop;
					}

				//Ok, check what registers are locked
				/*{
					String outReg = inst.getOutputRegister();
					if(outReg != null && !outReg.isEmpty())
					{
						Register r = reg.get(outReg);
						if(r.isLocked()) continue instructionLoop;
					}
					String[] inRegs = inst.getInputRegisters();
					if(inRegs != null && inRegs.length > 0)
					{
						for(String inReg : inRegs)
						{
							if(inReg == null) continue;
							Register r = reg.get(inReg);
							if(r.isLocked()) continue instructionLoop;
						}
					}
				}*/

				for(AbstractPipeline pl : pipeline)
					if(pl.addInstruction(inst))
					{
						for(AbstractPipeline pl2 : pipeline)
							pl2.increaseTimer();
						window.remove(inst);
						continue mainLoop;
					}
			}
		}
	}

	@Override
	protected void assignNext()
	{
		nextInstructionsToWindow();
		assignFromWindow();
	}
}
