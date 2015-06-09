package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;

public class Move extends AbstractOneLiteralOneOutInstruction
{
	public static IInstruction create(Conditional c, int id, String data)
	{
		String[] split = data.split(ReadingHelper.splitRegex,2);
		return new Move(c, id, split[0], split[1]);
	}

	public Move(Conditional _c, int id, String _out, String in1)
	{
		super(_c, id, _out, in1);
	}

	@Override
	public void doExecute()
	{
		super.doExecute();
		outVal = vals[0];
	}

	@Override
	public String getName()
	{
		return "MOV";
	}

}
