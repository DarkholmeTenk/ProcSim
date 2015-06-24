package io.darkcraft.procsim.view;

import io.darkcraft.procsim.controller.DataHelper;
import io.darkcraft.procsim.controller.DependencyGraphBuilder;
import io.darkcraft.procsim.controller.OutputController;
import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.helper.MapList;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.view.drawing.ColourStore;
import io.darkcraft.procsim.view.drawing.DrawingSurface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

	private JButton								leftButton;
	private JButton								rightButton;
	private JButton dependencyButton;

	private final AbstractSimulator				sim;
	private int									time			= 0;
	private int									maxTime;
	private int									stagesSkipped	= 0;
	private List<IInstruction[][]>				pipelineData;
	private MapList<IInstruction, IDependency>	deps;
	private String[][]							stageNames;
	private Set<Integer>						duplicateStages;
	private int depType = 2;

	public PipelineViewUI(AbstractSimulator _sim)
	{
		sim = _sim;
		pipelineData = sim.getMap();
		stageNames = sim.getStateNames();
		maxTime = pipelineData.size() - 1;
		duplicateStages = DataHelper.getDuplicateStates(pipelineData);
		deps = DependencyGraphBuilder.getToDependencies(sim.getInstructions());

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

		Font headFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		Font dataFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		for (int i = 0; i < stageNames.length; i++)
		{
			labels[i] = new JLabel[stageNames[i].length];
			for (int j = 0; j < stageNames[i].length; j++)
			{
				JLabel header = new JLabel(stageNames[i][j]);
				header.setFont(headFont);
				header.setHorizontalAlignment(SwingConstants.CENTER);
				slotPanel.add(header, GridBagHelper.setWeights(1, 0, GridBagHelper.getConstraints(i, j * 2)));
				JLabel data = new JLabel("");
				data.setFont(dataFont);
				data.setPreferredSize(labelSize);
				data.setOpaque(true);
				labels[i][j] = data;
				slotPanel.add(data, GridBagHelper.getConstraints(i, (j * 2) + 1));
			}
		}
		surface = new DrawingSurface();
		layered.setLayer(surface, 1, 2);
		layered.setLayer(slotPanel, 0, 0);
		layered.add(surface, GridBagHelper.getConstraints(0, 0));
		layered.add(slotPanel, GridBagHelper.getConstraints(0, 0));
		frame.add(layered, GridBagHelper.setWeights(1, GridBagHelper.getConstraints(0, 2, 5, 1)));
		update();
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
		for (int i = 0; i < labels.length; i++)
		{
			for (int j = 0; j < labels[i].length; j++)
			{
				JLabel label = labels[i][j];
				if (instructions[i][j] != null)
					label.setText(instructions[i][j].toString());
				else
					label.setText("");
				if (prev == null || prev[i][j] != instructions[i][j] || prev[i][j] == null)
				{
					if(j > sim.getLastIDStage(i) && instructions[i][j] != null && instructions[i][j].didFail())
						label.setForeground(OutputController.failedColor);
					else
						label.setForeground(Color.BLACK);
				}
				else
				{
					if(j == sim.getLastIDStage(i) && depType != 0)
						checkArrows(instructions,instructions[i][j],i,j);
					label.setForeground(OutputController.stalledColor);
				}
			}
		}
		surface.revalidate();
		surface.repaint();
		frame.pack();
		surface.setPreferredSize(slotPanel.getPreferredSize());
		frame.pack();
		frame.setMinimumSize(frame.getPreferredSize());
	}

	private void checkArrows(IInstruction[][] instructions, IInstruction inst, int x, int y)
	{
		List<IDependency> dependencies = deps.getList(inst);
		if(dependencies.size() == 0) return;
		int count = 0;
		for(int i = 0; i < instructions.length; i++)
		{
			for(int j = y; j < instructions[i].length; j++)
			{
				IInstruction to = instructions[i][j];
				if(to == null) continue;
				for(IDependency d : dependencies)
				{
					if(sim.isImportant(d.getType()) && d.getFrom() == to)
						addArrow(x,y,i,j,d);
				}
			}
		}
	}

	private void addArrow(int pl1, int st1, int pl2, int st2,IDependency d)
	{
		int i2 = map.containsKey(d.getFrom()) ? map.get(d.getFrom()) : 0;
		int i = mapTwo.containsKey(d.getTo()) ? mapTwo.get(d.getTo()) : 0;
		double x1 = pl1 * (labelSize.getWidth() + 8) + 50 - (i * 16);
		double y1 = st1 * (labelSize.getHeight()*2 + 7) + 8;
		double x2 = pl2 * (labelSize.getWidth() + 8) + 100 + (i2 * 16);
		double y2 = st2 * (labelSize.getHeight()*2 + 7) + 8;
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
		if (o == leftButton || o == rightButton)
		{
			int offset = (o == leftButton) ? -1 : 1;
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
