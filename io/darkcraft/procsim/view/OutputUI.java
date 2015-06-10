package io.darkcraft.procsim.view;

import io.darkcraft.procsim.model.helper.OutputHelper;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.awt.Color;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class OutputUI
{
	private AbstractSimulator sim;
	private JFrame mainFrame;
	private JScrollPane pane;
	private JPanel panel;

	public OutputUI(AbstractSimulator _sim)
	{
		mainFrame = new JFrame();
		mainFrame.setTitle("ProcSim Output");
		panel = new JPanel();
		panel.setLayout(GridBagHelper.getLayout());
		pane = new JScrollPane(panel);
		mainFrame.add(pane);
		sim = _sim;
		runSim();
		fillResults();
		mainFrame.pack();
		if(mainFrame.getWidth() > 1000 || mainFrame.getHeight() > 800)
		{
			mainFrame.setSize(Math.min(1000, mainFrame.getWidth()), Math.min(800, mainFrame.getHeight()));
		}
		mainFrame.setVisible(true);
	}

	private void runSim()
	{
		int timer = 0;
		while(sim.step())
		{
			if(timer++ > 50000)
				break;
		}
	}

	private void fillResults()
	{
		List<List<String>> data = OutputHelper.outputData(sim);
		for(int i = 0; i < data.size(); i++)
		{
			List<String> row = data.get(i);
			for(int j = 0; j < row.size(); j++)
			{
				String str = row.get(j);
				if(str.isEmpty()) continue;
				boolean green = str.startsWith("#");
				boolean red = str.startsWith("*");
				str = str.replace("#", "").replace("*", "");
				JLabel label = new JLabel(str);
				label.setOpaque(true);
				if(green)
					label.setForeground(Color.BLUE);
				if(red)
					label.setForeground(Color.RED);
				if(i % 2 == 0)
					label.setBackground(Color.WHITE);
				else
					label.setBackground(Color.LIGHT_GRAY);
				panel.add(label, GridBagHelper.getConstraints(j, i));
			}
		}
	}
}
