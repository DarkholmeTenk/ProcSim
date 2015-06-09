package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;


public class Add extends AbstractTwoLiteralOneOutInstruction
{
	public static IInstruction create(Conditional c, String data)
	{
		String[] split = data.split(ReadingHelper.splitRegex,3);
		return new Add(c, split[0], split[1], split[2]);
	}

	public Add(Conditional _c, String _out, String in1, String in2)
	{
		super(_c,_out, in1, in2);
	}

	@Override
	public void doExecute()
	{
		super.doExecute();
		outVal = vals[0] + vals[1];
	}

	@Override
	public void doMemory(IMemory mem)
	{
	}

	@Override
	public String toString()
	{
		return "ADD" + super.toString();
	}

}
