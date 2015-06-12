package io.darkcraft.procsim.view;

import io.darkcraft.procsim.controller.DependencyGraphBuilder;
import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.helper.KeyboardListener;
import io.darkcraft.procsim.model.helper.OutputHelper;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.view.drawing.DrawingSurface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class OutputUI implements ActionListener
{
	private AbstractSimulator sim;
	private JFrame mainFrame;
	private JPanel mainContainer;
	private JScrollPane pane;
	private JScrollPane instructionPane;
	private JPanel panel;
	private DrawingSurface surface;
	private JPanel instructionPanel;
	private JButton toggleArrowsButton;
	private JButton stateLeftButton;
	private JButton stateRightButton;
	private JLayeredPane layered;

	private int stateNum = 0;
	private int maxStateNum = 1;

	public OutputUI(AbstractSimulator _sim)
	{
		mainFrame = new JFrame();
		mainFrame.setTitle("ProcSim Output");
		mainContainer = new JPanel();
		mainContainer.setLayout(new BoxLayout(mainContainer,BoxLayout.LINE_AXIS));
		mainFrame.setLayout(GridBagHelper.getLayout());
		mainFrame.add(mainContainer, GridBagHelper.setWeights(1,GridBagHelper.getConstraints(1, 1, 10, 10)));
		panel = new JPanel();
		panel.setLayout(GridBagHelper.getLayout());
		instructionPanel = new JPanel();
		instructionPanel.setLayout(GridBagHelper.getLayout());
		surface = new DrawingSurface();
		layered = new JLayeredPane();
		layered.setLayout(GridBagHelper.getLayout());
		layered.setLayer(surface, 1, 2);
		layered.setLayer(panel, 0, 0);
		layered.add(panel, GridBagHelper.setWeights(0,GridBagHelper.getConstraints(0,0)),0);
		layered.add(surface, GridBagHelper.setWeights(0,GridBagHelper.getConstraints(0,0)),1);
		pane = new JScrollPane(layered);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		instructionPane = new JScrollPane(instructionPanel);
		instructionPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		instructionPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		instructionPane.getVerticalScrollBar().setModel(pane.getVerticalScrollBar().getModel());
		sim = _sim;
		runSim();
		addText("Instructions",0,0);
		fillInstructions();
		fillResults(stateNum = maxStateNum = sim.getFinalStateNum());
		mainContainer.add(instructionPane);
		mainFrame.pack();
		instructionPane.setMinimumSize(new Dimension(instructionPane.getWidth()+5, 1));
		mainContainer.add(pane);
		mainFrame.pack();
		pane.setPreferredSize(new Dimension(panel.getPreferredSize().width+5,panel.getPreferredSize().height+5));
		pane.setMinimumSize(new Dimension(Math.min(1000, mainFrame.getWidth()), Math.min(800, mainFrame.getHeight())));
		surface.setPreferredSize(panel.getPreferredSize());
		int x = (int) Math.round(pane.getPreferredSize().getWidth()) + instructionPane.getWidth() + 40;
		int y = pane.getPreferredSize().height;
		mainContainer.setPreferredSize(new Dimension(x,y));
		addOtherStuff(sim);
		mainFrame.pack();
		if(mainFrame.getWidth() > 1250 || mainFrame.getHeight() > 1000)
			mainFrame.setMinimumSize(new Dimension(Math.min(1000 + instructionPane.getWidth() + 20, mainFrame.getWidth()), Math.min(875, mainFrame.getHeight())));
		else
			mainFrame.setMinimumSize(mainFrame.getSize());
		mainFrame.pack();
		mainFrame.setSize(mainFrame.getMinimumSize());
		//mainFrame.setMinimumSize(mainFrame.getSize());
		mainFrame.setVisible(true);
		surface.revalidate();
	}

	private void addOtherStuff(AbstractSimulator sim)
	{
		toggleArrowsButton = new JButton("Toggle Arrows");
		toggleArrowsButton.addActionListener(this);
		mainFrame.add(toggleArrowsButton, GridBagHelper.getConstraints(9, 11, 2, 1));
		stateLeftButton = new JButton("<");
		stateLeftButton.addActionListener(this);
		mainFrame.add(stateLeftButton, GridBagHelper.getConstraints(7, 11, 1, 1));
		stateRightButton = new JButton(">");
		stateRightButton.addActionListener(this);
		mainFrame.add(stateRightButton, GridBagHelper.getConstraints(8, 11, 1, 1));
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
		if(!str.isEmpty())
			label.setOpaque(true);
		label.setFont(Font.getFont(Font.SANS_SERIF));
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

	private boolean isStalled(String str)
	{
		if(str == null) return false;
		return str.startsWith("@") || str.equals("...");
	}

	private void fillInstructions()
	{
		List<IInstruction> insts = sim.getInstructions();
		List<List<String>> data = OutputHelper.outputData(sim);
		for(int i = 0; i < data.size(); i++)
		{
			List<String> row = data.get(i);
			String str = row.get(0);
			if(str == null || str.isEmpty()) continue;
			addText(str,i,0);
		}
	}

	private void fillResults(int upTo)
	{
		List<IInstruction> insts = sim.getInstructions();
		Map<IInstruction,List<IDependency>> depMap = DependencyGraphBuilder.getToDependencies(insts);
		List<List<String>> data = OutputHelper.outputData(sim);
		boolean wasStalled = false;
		for(int i = 0; i < data.size(); i++)
		{
			List<String> row = data.get(i);
			for(int j = 1; j < row.size() && j <= upTo; j++)
			{
				String str = row.get(j);
				if(str == null || str.isEmpty()) continue;
				addText(str,i,j);
				if(i == 0) continue;
				boolean stalled = isStalled(str);
				int count = 1;
				if(!stalled && wasStalled)
				{
					IInstruction inst = insts.get(i-1);
					List<IDependency> originalDeps = depMap.get(inst);
					if(originalDeps == null) continue;
					List<IDependency> deps = new ArrayList<IDependency>();
					depPruneLoop:
					for(IDependency d : originalDeps)
					{
						for(IDependency e : deps)
							if(d.getFrom().equals(e.getFrom()))
								continue depPruneLoop;
						deps.add(d);
					}
					depLoop:
					for(IDependency d : deps)
					{
						IInstruction from = d.getFrom();
						int rowNum = 0;
						for(int rowCounter = 0; rowCounter < insts.size(); rowCounter++)
						{
							if(insts.get(rowCounter).equals(from))
							{
								rowNum = rowCounter;
								break;
							}
						}
						List<String> tempRow = data.get(rowNum+1);
						int finishTime = j-1;
						for(int x = j; x >= 0; x--)
						{
							if(!isStalled(row.get(x)) && x != j) continue depLoop;
							String strThing = tempRow.get(x);
							if(strThing!= null && !strThing.isEmpty())
							{
								if(x == j)
									continue depLoop;
								finishTime = x;
								break;
							}
						}
						double xOff = ((count / (deps.size() + 1.0)) * preferredSize.getWidth());
						double sX = (finishTime) * (preferredSize.getWidth() + 4) -3;
						double sY = (rowNum+1) * (preferredSize.getHeight() + 4) + 8;
						double eX = (j-1) * (preferredSize.getWidth() + 4) +3 + xOff;
						double eY = (i) * (preferredSize.getHeight() +4) + 2;
						surface.addArrow(sX, sY, eX, eY);
						count++;
					}
				}
				wasStalled = stalled;
			}
			for(int j = upTo+1; j < row.size(); j++)
				addText("",i,j);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == toggleArrowsButton)
			surface.setVisible(!surface.isVisible());
		if(source == stateLeftButton || source == stateRightButton)
		{
			panel.setVisible(false);
			surface.setVisible(false);
			int toChange = source == stateLeftButton ? -1 : 1;
			if(KeyboardListener.isCtrlDown())
				toChange *= KeyboardListener.isShiftDown() ? maxStateNum : 100;
			else
				toChange *= KeyboardListener.isShiftDown() ? 10 : 1;
			stateNum = Math.min(maxStateNum, Math.max(1, stateNum + toChange));
			panel.removeAll();
			surface.removeAll();
			fillResults(stateNum);
			panel.setVisible(true);
			surface.setVisible(true);
			panel.revalidate();
			layered.revalidate();
			surface.revalidate();
			surface.repaint();
		}
	}
}
