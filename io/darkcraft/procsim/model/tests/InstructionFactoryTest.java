package io.darkcraft.procsim.model.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import io.darkcraft.procsim.model.instruction.Conditional;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.InstructionFactory;

import org.junit.Test;

public class InstructionFactoryTest
{
	@Test
	public void Load()
	{
		IInstruction temp = InstructionFactory.get("LDR R1, x");
		assertNotNull("Failed on load x", temp);
		temp = InstructionFactory.get("LDR R5, [R2]");
		assertNotNull("Failed on load by register", temp);
		assertSame("Not AL cond", temp.getConditional(),Conditional.AL);
		temp = InstructionFactory.get("LDR R5, [R2, R3]");
		assertNotNull("Failed on load by register with register offset", temp);
		temp = InstructionFactory.get("LDR R5, [R2, #4]");
		assertNotNull("Failed on load by register with literal offset", temp);
		temp = InstructionFactory.get("LDRNE R8, x");
		assertNotNull("Failed on load by register with NE comparison", temp);
		assertSame("Not NE cond", temp.getConditional(),Conditional.NE);
		temp = InstructionFactory.get("LDRGT R9, [R2, R8]");
		assertNotNull("Failed on load by register with GT comparison", temp);
		assertSame("Not GT cond", temp.getConditional(),Conditional.GT);
	}

	@Test
	public void Add()
	{
		IInstruction temp = InstructionFactory.get("ADD R1, R2 R3");
		assertNotNull(temp);
		assertTrue(temp.getOutputRegister().equals("R1"));
		assertTrue(temp.getInputRegisters()[0].equals("R2"));
		assertTrue(temp.getInputRegisters()[1].equals("R3"));
		temp = InstructionFactory.get("ADD R1, R2 #3");
		assertNotNull(temp);
		assertTrue(temp.getOutputRegister().equals("R1"));
		assertTrue(temp.getInputRegisters()[0].equals("R2"));
		assertTrue(temp.getInputRegisters()[1] == null);
	}

	@Test
	public void B()
	{
		IInstruction temp = InstructionFactory.get("B test");
		assertNotNull(temp);
	}
}
