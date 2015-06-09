package io.darkcraft.procsim.model.instruction.instructions;

import io.darkcraft.procsim.model.instruction.Conditional;

public class Nop extends Move
{

	public Nop(Conditional _c, int id)
	{
		super(_c, id, "R0", "R0");
	}

}
