package io.darkcraft.procsim.view.pipeline;

import io.darkcraft.procsim.controller.DataHelper;
import io.darkcraft.procsim.controller.DependencyGraphBuilder;
import io.darkcraft.procsim.controller.MemoryState;
import io.darkcraft.procsim.controller.OutputController;
import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.helper.KeyboardListener;
import io.darkcraft.procsim.model.helper.MapList;
import io.darkcraft.procsim.model.helper.MiscFunctions;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.instruction.IMemoryInstruction;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.view.GridBagHelper;
import io.darkcraft.procsim.view.LabelHelper;
import io.darkcraft.procsim.view.drawing.ColourStore;
import io.darkcraft.procsim.view.drawing.DrawingSurface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PipelineViewUI implements ActionListener
{
	private JFrame								frame;
	private JLabel[][]							labels;
	private JLabel								timeLabel;
	private static Dimension					labelSize		= new Dimension(150, 16);
	private DrawingSurface						surface;
	private JPanel								slotPanel;
	private JPanel								memoryPanel;
	private InstructionWindowPanel				iwPanel;

	private JButton								leftButton;
	private JButton								rightButton;
	private JButton dependencyButton;

	private final AbstractSimulator				sim;
	private int									time			= 0;
	private int									maxTime;
	private int									stagesSkipped	= 0;
	private List<IInstruction[][]>				pipelineData;
	private List<MemoryState>					memoryData;
	private MapList<IInstruction, IDependency>	deps;
	private String[][]							stageNames;
	private Set<Integer>						duplicateStages;
	private int depType = 2;
	private int[][] exeBlocks;
	private int exeWidth;

	public PipelineViewUI(AbstractSimulator _sim)
	{
		sim = _sim;
		pipelineData = sim.getStateTimeline();
		memoryData = sim.getMemoryTimeline();
		stageNames = sim.getStateNames();
		maxTime = pipelineData.size() - 1;
		duplicateStages = DataHelper.getDuplicateStates(pipelineData);
		deps = DependencyGraphBuilder.getToDependencies(sim.getInstructions());
		exeBlocks = sim.getExeBlocks();
		exeWidth = exeBlocks.length;

		frame = new JFrame();
		frame.setLayout(GridBagHelper.getLayout());
		draw();
		frame.pack();
		frame.setTitle("ProcSim - Pipeline View");
		frame.setVisible(true);
	}

	private void draw()
	{
		leftButton = new JButton("<");
		leftButton.addActionListener(this);
		rightButton = new JButton(">");
		rightButton.addActionListener(this);
		frame.add(leftButton, GridBagHelper.getConstraints(1, 1));
		frame.add(rightButton, GridBagHelper.getConstraints(3, 1));
		dependencyButton = new JButton("Show Dependencies");
		dependencyButton.addActionListener(this);
		frame.add(dependencyButton, GridBagHelper.setWeights(1, 0, GridBagHelper.getConstraints(2, 1, 1, 1)));
		timeLabel = new JLabel("Cycle: " + time);
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.add(timeLabel, GridBagHelper.setWeights(1, 0, GridBagHelper.getConstraints(1, 0, 3, 1, GridBagConstraints.CENTER)));

		JLayeredPane layered = new JLayeredPane();
		layered.setLayout(GridBagHelper.getLayout());
		slotPanel = new JPanel();
		slotPanel.setLayout(GridBagHelper.getLayout());
		labels = new JLabel[stageNames.length][];



		for (int i = 0; i < stageNames.length; i++)
		{
			labels[i] = new JLabel[stageNames[i].length];
			for (int j = 0; j < stageNames[i].length; j++)
			{
				if(MiscFunctions.in(exeBlocks, j) != -1) continue;
				JLabel header = LabelHelper.get(stageNames[i][j]);
				header.setHorizontalAlignment(SwingConstants.CENTER);
				int xPos = (i * exeWidth) + (exeWidth/2);
				slotPanel.add(header, GridBagHelper.setWeights(1, 0, GridBagHelper.getConstraints(xPos, j * 2)));
				JLabel data = LabelHelper.getPlain("",labelSize,true);
				labels[i][j] = data;
				slotPanel.add(data, GridBagHelper.getConstraints(xPos, (j * 2) + 1));
			}
			int block = 0;
			int yPos = MiscFunctions.min(exeBlocks);
			for(int[] exeBlock : exeBlocks)
			{
				int lastStage = exeBlock[0];
				JLabel header = LabelHelper.get(stageNames[i][lastStage]);
				header.setHorizontalAlignment(SwingConstants.CENTER);
				int xPos = (i * exeWidth) + (block++);
				slotPanel.add(header, GridBagHelper.setWeights(1, 0, GridBagHelper.getConstraints(xPos, yPos * 2)));
				JLabel data = LabelHelper.getPlain("",labelSize,true);
				for(int stage : exeBlock)
					labels[i][stage] = data;
				slotPanel.add(data, GridBagHelper.getConstraints(xPos, (yPos * 2) + 1));
			}
		}
		surface = new DrawingSurface();
		layered.setLayer(surface, 1, 2);
		layered.setLayer(slotPanel, 0, 0);
		layered.add(surface, GridBagHelper.getConstraints(0, 0));
		layered.add(slotPanel, GridBagHelper.getConstraints(0, 0));
		frame.add(layered, GridBagHelper.setWeights(1, GridBagHelper.getConstraints(0, 2, 5, 2)));
		memoryPanel = new JPanel();
		memoryPanel.setLayout(GridBagHelper.getLayout());
		frame.add(memoryPanel, GridBagHelper.getConstraints(6, 2, 1, 2));
		if(sim.hasInstructionWindow())
		{
			iwPanel = new InstructionWindowPanel(sim);
			frame.add(iwPanel, GridBagHelper.getConstraints(7,2,1,1, GridBagConstraints.NORTH));
			JLabel temp = LabelHelper.get("");
			frame.add(temp, GridBagHelper.setWeights(1, GridBagHelper.getConstraints(7, 3)));
		}
		update();
	}

	private boolean showDep(int time, int pl, int st)
	{
		if((time == 0) || (time >= (maxTime-2))) return false;
		if(st >= (stageNames[pl].length - 1)) return false;
		IInstruction[][] instructions = pipelineData.get(time);
		IInstruction inst = instructions[pl][st];
		if(inst instanceof IMemoryInstruction)
			if(stageNames[pl][st].equals("MEM"))
				return false;
		if(instructions[pl][st+1] != null)
			return false;
		return true;
	}

	private HashMap<IInstruction,Integer> map;
	private HashMap<IInstruction,Integer> mapTwo;
	private void update()
	{
		surface.clear();
		IInstruction[][] instructions = pipelineData.get(time);
		IInstruction[][] prev = null;
		if (time > 0)
			prev = pipelineData.get(time - 1);
		if (stagesSkipped == 0)
			timeLabel.setText("Cycle: " + time);
		else
			timeLabel.setText("Cycle: " + time + " (skipped " + stagesSkipped + ")");
		map = new HashMap<IInstruction,Integer>();
		mapTwo = new HashMap<IInstruction,Integer>();
		for (JLabel[] ls : labels)
			for(JLabel l :ls)
				l.setText("");
		for (int i = 0; i < labels.length; i++)
		{
			for (int j = 0; j < labels[i].length; j++)
			{
				JLabel label = labels[i][j];
				if (instructions[i][j] != null)
					label.setText(instructions[i][j].toString());
				if ((prev == null) || (prev[i][j] != instructions[i][j]) || (prev[i][j] == null))
				{
					if((j > sim.getLastIDStage(i)) && (instructions[i][j] != null) && instructions[i][j].didFail())
						label.setForeground(OutputController.failedColor);
					else
						label.setForeground(Color.BLACK);
				}
				else
				{
					//if(j == sim.getLastIDStage(i) && depType != 0)
					if(showDep(time,i,j) && (depType != 0))
						checkArrows(instructions,instructions[i][j],i,j);
					label.setForeground(OutputController.stalledColor);
				}
			}
		}
		surface.revalidate();
		surface.repaint();
		frame.pack();
		/*
		 * Memory panel
		 */
		memoryPanel.setVisible(false);
		memoryPanel.removeAll();
		MemoryState m = memoryData.get(time);
		int i = 0;
		while(m != null)
		{
			int y = 5 * i;
			JLabel label = LabelHelper.get(m.mem.toString());
			memoryPanel.add(label, GridBagHelper.getConstraints(0,y,2,1));
			memoryPanel.add(LabelHelper.getPlain("Reads:"), GridBagHelper.getConstraints(0,y+1));
			memoryPanel.add(LabelHelper.getPlain(""+m.reads),GridBagHelper.getConstraints(1,y+1));
			memoryPanel.add(LabelHelper.getPlain("Writes:"), GridBagHelper.getConstraints(0,y+2));
			memoryPanel.add(LabelHelper.getPlain(""+m.writes),GridBagHelper.getConstraints(1,y+2));
			if(m.conflicts > 0)
			{
				memoryPanel.add(LabelHelper.getPlain("Conflicts:"), GridBagHelper.getConstraints(0,y+3));
				memoryPanel.add(LabelHelper.getPlain(""+m.conflicts),GridBagHelper.getConstraints(1,y+3));
			}
			if(m.misses > 0)
			{
				memoryPanel.add(LabelHelper.getPlain("Misses:"), GridBagHelper.getConstraints(0,y+4));
				memoryPanel.add(LabelHelper.getPlain(""+m.misses),GridBagHelper.getConstraints(1,y+4));
			}
			m = m.nextLevel;
			i++;
		}
		memoryPanel.revalidate();
		memoryPanel.setVisible(true);
		frame.revalidate();
		/*
		 * Instruction window
		 */
		if(iwPanel != null)
			iwPanel.update(time);
		/*
		 * Repack
		 */
		frame.pack();
		surface.setPreferredSize(slotPanel.getPreferredSize());
		frame.pack();
		frame.setMinimumSize(frame.getPreferredSize());
	}

	private void checkArrows(IInstruction[][] instructions, IInstruction inst, int x, int y)
	{
		List<IDependency> dependencies = deps.getList(inst);
		if(dependencies.size() == 0) return;
		for(int i = 0; i < instructions.length; i++)
		{
			for(int j = y; j < instructions[i].length; j++)
			{
				IInstruction to = instructions[i][j];
				if(to == null) continue;
				for(IDependency d : dependencies)
				{
					if(sim.isImportant(d.getType()) && (d.getFrom() == to))
						addArrow(x,y,i,j,d);
				}
			}
		}
	}

	private void addArrow(int pl1, int st1, int pl2, int st2,IDependency d)
	{
		int i2 = map.containsKey(d.getFrom()) ? map.get(d.getFrom()) : 0;
		int i = mapTwo.containsKey(d.getTo()) ? mapTwo.get(d.getTo()) : 0;
		double x1 = ((pl1 * (labelSize.getWidth() + 8)) + 50) - (i * 16);
		double y1 = (st1 * ((labelSize.getHeight()*2) + 7)) + 8;
		double x2 = (pl2 * (labelSize.getWidth() + 8)) + 100 + (i2 * 16);
		double y2 = (st2 * ((labelSize.getHeight()*2) + 7)) + 8;
		if(y1 == y2)
		{
			y1 -= 2;
			y2 += 2;
		}
		map.put(d.getFrom(), i2+1);
		mapTwo.put(d.getTo(), i+1);
		if(depType == 2)
			surface.addStar(x2, y2, x1, y1, ColourStore.getColor(d.getDependentRegister()), d.getType());
		else
			surface.addArrow(x2, y2, x1, y1, ColourStore.getColor(d.getDependentRegister()), d.getType());
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object o = e.getSource();
		if ((o == leftButton) || (o == rightButton))
		{
			int offset = (o == leftButton) ? -1 : 1;
			offset *= KeyboardListener.getMultiplier(maxTime);
			time = Math.max(0, Math.min(maxTime, time + offset));
			stagesSkipped = 0;
			while (duplicateStages.contains(time))
			{
				stagesSkipped++;
				time = Math.max(0, Math.min(maxTime, time + offset));
			}
			update();
		}
		if (o == dependencyButton)
		{
			depType = (depType + 1) % 3;
			update();
		}
	}

	public boolean isVisible()
	{
		return frame.isVisible();
	}
}
