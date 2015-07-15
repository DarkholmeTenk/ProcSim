package io.darkcraft.procsim.view.pipeline;

import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.view.GridBagHelper;
import io.darkcraft.procsim.view.LabelHelper;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class InstructionWindowPanel extends JPanel
{
	private final AbstractSimulator sim;

	public InstructionWindowPanel(AbstractSimulator _sim)
	{
		sim = _sim;
		setLayout(GridBagHelper.getLayout());
	}

	public void update(int stage)
	{
		List<IInstruction> instructions = sim.getInstructionWindow(stage);
		removeAll();
		int i = 0;
		for(IInstruction inst : instructions)
		{
			JLabel label = LabelHelper.getPlain(inst.toString());
			add(label, GridBagHelper.getConstraints(0, ++i));
		}
		add(LabelHelper.get("Instruction Window"), GridBagHelper.getConstraints());
	}

}
