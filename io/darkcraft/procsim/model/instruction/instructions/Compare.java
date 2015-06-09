package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.components.abstracts.IMemory;
import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;

public class Compare extends AbstractTwoLiteralOneOutInstruction
{
	public static IInstruction create(Conditional c, String data)
	{
		String[] split = data.split(ReadingHelper.splitRegex,2);
		return new Compare(c, split[0], split[1]);
	}

	public Compare(Conditional _c, String in1, String in2)
	{
		super(_c, "CPSR", in1, in2);
	}

	@Override
	public void doExecute()
	{
		super.doExecute();
		int x = vals[0] - vals[1];
		if(x == 0)
			outVal = Conditional.zerMask;
		else if(x < 0)
			outVal = Conditional.negMask;
		else
			outVal = 0;
	}

	@Override
	public void doMemory(IMemory mem)
	{
	}

	@Override
	public String toString()
	{
		return "CMP" + c + toString(0) + toString(1);
	}

}
