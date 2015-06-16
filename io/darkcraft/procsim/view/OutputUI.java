package io.darkcraft.procsim.view;

import io.darkcraft.procsim.controller.DependencyType;
import io.darkcraft.procsim.controller.OutputController;
import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.helper.KeyboardListener;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.view.drawing.DrawingSurface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class OutputUI implements ActionListener
{
	public AbstractSimulator	sim;
	private JFrame				mainFrame;
	private JPanel				mainContainer;
	private JScrollPane			pane;
	private JScrollPane			instructionPane;
	public final JPanel			dataPanel;
	public DrawingSurface		surface;
	public final JPanel			instructionPanel;
	private JButton				toggleArrowsButton;
	private JButton				stateLeftButton;
	private JButton				stateRightButton;
	private JLayeredPane		layered;
	// 1 = arrows, 2 = stars, 0 = none
	public int					dependencyDisplay	= 1;

	public int					stateNum			= 0;
	private int					maxStateNum			= 1;
	public boolean[]			importantDependencyType;
	public OutputController		controller;

	private static final Color bg = Color.DARK_GRAY;
	public OutputUI(AbstractSimulator _sim)
	{
		controller = new OutputController(this, _sim);
		importantDependencyType = new boolean[DependencyType.values().length];
		for (DependencyType dt : DependencyType.values())
			importantDependencyType[dt.ordinal()] = _sim.isImportant(dt);
		mainFrame = new JFrame();
		mainFrame.setTitle("ProcSim Output");
		mainContainer = new JPanel();
		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.LINE_AXIS));
		mainFrame.setLayout(GridBagHelper.getLayout());
		mainFrame.add(mainContainer, GridBagHelper.setWeights(1, GridBagHelper.getConstraints(1, 1, 10, 10)));
		dataPanel = new JPanel();
		dataPanel.setBackground(bg);
		dataPanel.setLayout(GridBagHelper.getLayout());
		instructionPanel = new JPanel();
		instructionPanel.setBackground(bg);
		instructionPanel.setLayout(GridBagHelper.getLayout());
		surface = new DrawingSurface();
		layered = new JLayeredPane();
		layered.setBackground(bg);
		layered.setOpaque(true);
		layered.setLayout(GridBagHelper.getLayout());
		layered.setLayer(surface, 1, 2);
		layered.setLayer(dataPanel, 0, 0);
		layered.add(dataPanel, GridBagHelper.setWeights(0, GridBagHelper.getConstraints(0, 0)), 0);
		layered.add(surface, GridBagHelper.setWeights(0, GridBagHelper.getConstraints(0, 0)), 1);
		pane = new JScrollPane(layered);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		instructionPane = new JScrollPane(instructionPanel);
		instructionPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		instructionPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		instructionPane.getVerticalScrollBar().setModel(pane.getVerticalScrollBar().getModel());
		sim = _sim;
		runSim();
		controller.addText("Instructions", 0, 0);
		controller.fillInstructions();
		controller.fillResults(stateNum = maxStateNum = sim.getFinalStateNum());
		controller.addArrowsToSurface();
		mainContainer.add(instructionPane);
		mainFrame.pack();
		instructionPane.setMinimumSize(new Dimension(instructionPane.getWidth() + 5, 1));
		mainContainer.add(pane);
		mainFrame.pack();
		pane.setPreferredSize(new Dimension(dataPanel.getPreferredSize().width + 5, dataPanel.getPreferredSize().height + 22));
		pane.setMinimumSize(new Dimension(Math.min(1000, mainFrame.getWidth()), Math.min(800, mainFrame.getHeight())));
		surface.setPreferredSize(dataPanel.getPreferredSize());
		int x = (int) Math.round(pane.getPreferredSize().getWidth()) + instructionPane.getWidth() + 40;
		int y = pane.getPreferredSize().height;
		mainContainer.setPreferredSize(new Dimension(x, y));
		addOtherStuff(sim);
		mainFrame.pack();
		if (mainFrame.getWidth() > 1250 || mainFrame.getHeight() > 1000)
			mainFrame.setMinimumSize(new Dimension(Math.min(1000 + instructionPane.getWidth() + 20, mainFrame.getWidth()), Math.min(875, mainFrame.getHeight())));
		else
			mainFrame.setMinimumSize(mainFrame.getSize());
		mainFrame.pack();
		mainFrame.setSize(mainFrame.getMinimumSize());
		// mainFrame.setMinimumSize(mainFrame.getSize());
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
		while (sim.step())
		{
			if (timer++ > 50000)
				break;
		}
	}

	public static Dimension		preferredSize		= null;
	public static Dimension		gapSize				= new Dimension(4, 1);

	public static final boolean	doNotStallingDeps	= false;

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == toggleArrowsButton)
		{
			dependencyDisplay++;
			dependencyDisplay = dependencyDisplay % 3;
			controller.addArrowsToSurface();
		}
		if (source == stateLeftButton || source == stateRightButton)
		{
			dataPanel.setVisible(false);
			surface.setVisible(false);
			int toChange = source == stateLeftButton ? -1 : 1;
			if (KeyboardListener.isCtrlDown())
				toChange *= KeyboardListener.isShiftDown() ? maxStateNum : 100;
			else
				toChange *= KeyboardListener.isShiftDown() ? 10 : 1;
			stateNum = Math.min(maxStateNum, Math.max(1, stateNum + toChange));
			dataPanel.removeAll();
			controller.clear();
			controller.fillResults(stateNum);
			controller.addArrowsToSurface();
			dataPanel.setVisible(true);
			surface.setVisible(true);
			dataPanel.revalidate();
			layered.revalidate();
		}
	}

	public static class ArrowDataStore
	{
		public final int			startX, startY, endX, endY, count, maxCount;
		public final IDependency	dep;

		public ArrowDataStore(int x1, int y1, int x2, int y2, IDependency d, int num, int max)
		{
			startX = x1;
			startY = y1;
			endX = x2;
			endY = y2;
			dep = d;
			count = num;
			maxCount = max;
		}
	}
}
