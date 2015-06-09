package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;

public class Test extends AbstractTwoLiteralOneOutInstruction
{
	public static IInstruction create(Conditional c, int id, String data)
	{
		String[] split = data.split(ReadingHelper.splitRegex,2);
		return new Test(c, id, split[0], split[1]);
	}

	public Test(Conditional _c, int id, String in1, String in2)
	{
		super(_c, id, "CPSR", in1, in2);
	}

	@Override
	public void doExecute()
	{
		int x = vals[0] & vals[1];
		if(x == 0)
			outVal = Conditional.zerMask;
		else
			outVal = 0;
	}

	@Override
	public String getName()
	{
		return "TST";
	}

}
