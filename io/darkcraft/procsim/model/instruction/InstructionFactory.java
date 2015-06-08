package io.darkcraft.procsim.model.instruction;

import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.instructions.Add;
import io.darkcraft.procsim.model.instruction.instructions.Branch;
import io.darkcraft.procsim.model.instruction.instructions.Compare;
import io.darkcraft.procsim.model.instruction.instructions.Load;
import io.darkcraft.procsim.model.instruction.instructions.Move;
import io.darkcraft.procsim.model.instruction.instructions.Sub;

public class InstructionFactory
{
	public static IInstruction get(String line)
	{
		int comment = line.indexOf(';');
		if(comment != -1)
			line = line.substring(0,comment);
		String[] data = line.split(ReadingHelper.splitRegex,2);
		data[0] = data[0].toUpperCase();
		Conditional c = Conditional.get(data[0]);
		if(data[0].startsWith("LDR"))
			return Load.create(c,data[1]);
		if(data[0].startsWith("ADD"))
			return Add.create(c,data[1]);
		if(data[0].startsWith("SUB"))
			return Sub.create(c,data[1]);
		if(data[0].startsWith("MOV"))
			return Move.create(c,data[1]);
		if(data[0].startsWith("CMP"))
			return Compare.create(c,data[1]);
		if(data[0].startsWith("B"))
			return new Branch(c,data[1]);
		return null;
	}
}
