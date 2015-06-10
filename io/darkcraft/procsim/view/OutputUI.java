package io.darkcraft.procsim.view;

import io.darkcraft.procsim.model.helper.OutputHelper;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class OutputUI
{
	private AbstractSimulator sim;
	private JFrame mainFrame;
	private JScrollPane pane;
	private JScrollPane instructionPane;
	private JPanel panel;
	private JPanel instructionPanel;

	public OutputUI(AbstractSimulator _sim)
	{
		mainFrame = new JFrame();
		mainFrame.setTitle("ProcSim Output");
		mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane(),BoxLayout.LINE_AXIS));
		panel = new JPanel();
		panel.setLayout(GridBagHelper.getLayout());
		instructionPanel = new JPanel();
		instructionPanel.setLayout(GridBagHelper.getLayout());
		pane = new JScrollPane(panel);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		instructionPane = new JScrollPane(instructionPanel);
		instructionPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		instructionPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		instructionPane.getVerticalScrollBar().setModel(pane.getVerticalScrollBar().getModel());
		sim = _sim;
		runSim();
		addText("Instructions",0,0);
		fillResults();
		mainFrame.add(instructionPane);
		mainFrame.pack();
		instructionPane.setMinimumSize(new Dimension(instructionPane.getWidth()+5, 1));
		mainFrame.add(pane);
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

	private void addText(String str, int i, int j)
	{
		boolean green = str.startsWith("#");
		boolean red = str.startsWith("*");
		boolean purp = str.startsWith("@");
		str = str.replace("#", "").replace("*", "").replace("@", "");
		JLabel label = new JLabel(str);
		if(preferredSize == null)
			preferredSize = new Dimension(35,label.getPreferredSize().height);
		if(j > 0)
			label.setPreferredSize(preferredSize);
		label.setOpaque(true);
		if(green)
			label.setForeground(Color.BLUE);
		if(red)
			label.setForeground(Color.RED);
		if(purp)
			label.setForeground(Color.getHSBColor(0.8f, 1, 1));
		if(i % 2 == 0)
			label.setBackground(Color.WHITE);
		else
			label.setBackground(Color.LIGHT_GRAY);
		if(j > 0)
			panel.add(label, GridBagHelper.getConstraints(j, i));
		else
			instructionPanel.add(label, GridBagHelper.getConstraints(j, i));
	}


	private static Dimension preferredSize = null;
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
				addText(str,i,j);
			}
		}
	}
}
