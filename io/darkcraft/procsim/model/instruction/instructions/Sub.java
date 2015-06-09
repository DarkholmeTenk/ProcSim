package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;

public class Sub extends AbstractTwoLiteralOneOutInstruction
{

	public static IInstruction create(Conditional c, int id, String data)
	{
		String[] split = data.split(ReadingHelper.splitRegex,3);
		return new Sub(c, id, split[0], split[1], split[2]);
	}

	public Sub(Conditional _c, int id, String _out, String in1, String in2)
	{
		super(_c, id, _out, in1, in2);
	}

	@Override
	public void doExecute()
	{
		super.doExecute();
		outVal = vals[0] - vals[1];
	}

	@Override
	public String toString()
	{
		return "SUB" + super.toString();
	}

	@Override
	public String getName()
	{
		return "SUB";
	}
}
