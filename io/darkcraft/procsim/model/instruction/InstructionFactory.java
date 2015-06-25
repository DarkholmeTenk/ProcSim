package io.darkcraft.procsim.model.instruction;

import io.darkcraft.procsim.model.helper.ReadingHelper;
import io.darkcraft.procsim.model.instruction.instructions.Add;
import io.darkcraft.procsim.model.instruction.instructions.Branch;
import io.darkcraft.procsim.model.instruction.instructions.BranchLink;
import io.darkcraft.procsim.model.instruction.instructions.Compare;
import io.darkcraft.procsim.model.instruction.instructions.Load;
import io.darkcraft.procsim.model.instruction.instructions.Move;
import io.darkcraft.procsim.model.instruction.instructions.Mul;
import io.darkcraft.procsim.model.instruction.instructions.Nop;
import io.darkcraft.procsim.model.instruction.instructions.Store;
import io.darkcraft.procsim.model.instruction.instructions.Sub;

public class InstructionFactory
{
	private static String strip(String in, Conditional con)
	{
		String suffix = con.getName();
		if(in.endsWith(suffix))
			return in.substring(0, in.length()-suffix.length());
		return in;
	}

	public static IInstruction get(String line, int id)
	{
		line = line.trim();
		int comment = line.indexOf(';');
		if(comment == 0) return null;
		if(comment != -1)
			line = line.substring(0,comment);
		line = line.trim();
		String[] data = line.split(ReadingHelper.splitRegex,2);
		data[0] = data[0].toUpperCase();
		Conditional c = Conditional.get(data[0]);
		data[0] = strip(data[0],c);
		if(data[0].startsWith("LDR"))
			return Load.create(c,id,data[1]);
		if(data[0].startsWith("STR"))
			return Store.create(c, id, data[1]);
		if(data[0].startsWith("ADD"))
			return Add.create(c,id,data[1]);
		if(data[0].startsWith("SUB"))
			return Sub.create(c,id,data[1]);
		if(data[0].startsWith("MOV"))
			return Move.create(c,id,data[1]);
		if(data[0].startsWith("MUL"))
			return Mul.create(c, id, data[1]);
		if(data[0].startsWith("CMP"))
			return Compare.create(c,id,data[1]);
		if(data[0].startsWith("BL"))
			return BranchLink.create(c,id,data[1]);
		if(data[0].startsWith("B"))
			return new Branch(c,id,data[1]);
		if(data[0].startsWith("NOP"))
			return new Nop(c,id);
		return null;
	}
}
