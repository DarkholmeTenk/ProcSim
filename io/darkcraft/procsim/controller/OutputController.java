package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.helper.OutputHelper;
import io.darkcraft.procsim.model.helper.Pair;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.view.GridBagHelper;
import io.darkcraft.procsim.view.OutputUI;
import io.darkcraft.procsim.view.OutputUI.ArrowDataStore;
import io.darkcraft.procsim.view.drawing.ColourStore;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class OutputController
{
	private OutputUI								toFill;
	private AbstractSimulator						simulator;
	private List<List<Pair<IInstruction, String>>>	fillData	= null;
	private List<IInstruction>						insts		= null;
	private Map<Integer, List<ArrowDataStore>>		links		= new HashMap();

	public OutputController(OutputUI parent, AbstractSimulator sim)
	{
		toFill = parent;
		simulator = sim;
	}

	public void fillInstructions()
	{
		if (fillData == null)
			fillData = OutputHelper.outputData(simulator);
		for (int i = 0; i < fillData.size(); i++)
		{
			List<Pair<IInstruction, String>> row = fillData.get(i);
			Pair<IInstruction, String> pair = row.get(0);
			if (pair == null || pair.b == null || pair.b.isEmpty())
				continue;
			addText(pair.b, i, 0);
		}
	}

	public void addText(String str, int i, int j)
	{
		boolean green = str.startsWith("#");
		boolean red = str.startsWith("*");
		boolean purp = str.startsWith("@");
		str = str.replace("#", "").replace("*", "").replace("@", "");
		JLabel label = new JLabel(str);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		if (OutputUI.preferredSize == null)
			OutputUI.preferredSize = new Dimension(36, label.getPreferredSize().height);
		if (j > 0)
			label.setPreferredSize(OutputUI.preferredSize);
		if (!str.isEmpty())
			label.setOpaque(true);
		label.setFont(Font.getFont(Font.SANS_SERIF));
		if (green)
			label.setForeground(Color.BLUE);
		if (red)
			label.setForeground(Color.RED);
		if (purp)
			label.setForeground(Color.getHSBColor(0.8f, 1, 1));
		if (i % 2 == 0)
			label.setBackground(Color.WHITE);
		else
			label.setBackground(Color.LIGHT_GRAY);
		if (j > 0)
			toFill.dataPanel.add(label, GridBagHelper.getConstraints(2 * j, i));
		else
			toFill.instructionPanel.add(label, GridBagHelper.getConstraints(j, i));
	}

	public void fillResults(int upTo)
	{
		if (insts == null)
			insts = simulator.getInstructions();
		Map<IInstruction, List<IDependency>> depMap = DependencyGraphBuilder.getToDependencies(insts);
		if (fillData == null)
			fillData = OutputHelper.outputData(simulator);
		boolean wasStalled = false;
		int lowestY = 0;
		for (int i = 0; i < fillData.size(); i++)
		{
			List<Pair<IInstruction, String>> row = fillData.get(i);
			for (int j = 1; j < row.size() && j <= upTo; j++)
			{
				if (i == 0)
				{
					JLabel gap = new JLabel(" ");
					gap.setPreferredSize(OutputUI.gapSize);
					toFill.dataPanel.add(gap, GridBagHelper.getConstraints(2 * j + 1, 0));
				}
				Pair<IInstruction, String> pair = row.get(j);
				if (pair == null || pair.b == null || pair.b.isEmpty())
					continue;
				String str = pair.b;
				addText(str, i, j);
				lowestY = i;
				if (i == 0)
					continue;
				boolean stalled = isStalled(str);
				if (!stalled && wasStalled)
					fillArrows(insts, depMap, fillData, i, j);
				wasStalled = stalled;
			}

		}
		JLabel gap = new JLabel(" ");
		gap.setPreferredSize(OutputUI.gapSize);
		toFill.dataPanel.add(gap, GridBagHelper.setWeights(1,0,GridBagHelper.getConstraints(2 * upTo + 1, 0)));
		if(upTo < fillData.get(0).size()-1)
		{
			JLabel l = new JLabel("");
			toFill.dataPanel.add(l, GridBagHelper.setWeights(0,1,GridBagHelper.getConstraints(2 * upTo + 1, lowestY+1)));
		}
	}

	public void fillArrows(List<IInstruction> insts, Map<IInstruction, List<IDependency>> depMap, List<List<Pair<IInstruction, String>>> data, int i, int j)
	{
		int count = 1;
		List<Pair<IInstruction, String>> row = data.get(i);
		IInstruction inst = row.get(0).a;
		List<IDependency> originalDeps = depMap.get(inst);
		if (originalDeps == null)
			return;
		List<IDependency> deps = new ArrayList<IDependency>();
		depPruneLoop: for (IDependency d : originalDeps)
		{
			for (IDependency e : deps)
				if (d.getFrom().equals(e.getFrom()))
					continue depPruneLoop;
			deps.add(d);
		}
		depLoop: for (IDependency d : deps)
		{
			if (!toFill.importantDependencyType[d.getType().ordinal()])
				continue;
			IInstruction from = d.getFrom();
			for (int rowNum = i; rowNum > 0; rowNum--)
			{
				List<Pair<IInstruction, String>> tempRow = data.get(rowNum);
				if (!tempRow.get(0).a.equals(from))
					continue;
				int finishTime = -1;
				for (int x = j; x >= (OutputUI.doNotStallingDeps ? 0 : j - 1); x--)
				{
					Pair<IInstruction, String> pair = row.get(x);
					Pair<IInstruction, String> pair2 = tempRow.get(x);
					if ((pair != null && !isStalled(pair.b)) && x != j)
						continue depLoop;
					if (pair2 != null && pair2.b != null && !pair2.b.isEmpty())
					{
						if (x == j)
							continue depLoop;
						finishTime = x;
						break;
					}
				}
				if (finishTime == -1)
					continue depLoop;
				if (!links.containsKey(j))
					links.put(j, new ArrayList());
				List<ArrowDataStore> arrows = links.get(j);
				arrows.add(new ArrowDataStore(finishTime, rowNum, j, i, d, count, 1));
			}
		}
	}

	public void addArrowsToSurface()
	{
		toFill.surface.clear();
		if (toFill.dependencyDisplay == 1)
		{
			for (Integer i : links.keySet())
			{
				if (i > toFill.stateNum)
					continue;
				List<ArrowDataStore> arrows = links.get(i);
				for(ArrowDataStore arrow : arrows)
				{
					double x1 = arrow.startX * (OutputUI.preferredSize.getWidth() + 8 + OutputUI.gapSize.getWidth()) - OutputUI.gapSize.getWidth() - 7;
					double y1 = (arrow.startY+1) * (OutputUI.preferredSize.getHeight() + 4) - 8;
					double x2 = (arrow.endX - 1) * (OutputUI.preferredSize.getWidth() + 8 + OutputUI.gapSize.getWidth()) + 3;
					double y2 = arrow.endY * (OutputUI.preferredSize.getHeight() + 4) +3;
					Color c = ColourStore.getColor(arrow.dep.getDependentRegister());
					toFill.surface.addArrow(x1, y1, x2, y2, c, arrow.dep.getType());
				}
			}
		}
		else if(toFill.dependencyDisplay == 2)
		{

			for (Integer i : links.keySet())
			{
				if (i > toFill.stateNum)
					continue;
				List<ArrowDataStore> arrows = links.get(i);
				for(ArrowDataStore arrow : arrows)
				{
					double x1 = arrow.startX * (OutputUI.preferredSize.getWidth() + 8 + OutputUI.gapSize.getWidth()) - OutputUI.gapSize.getWidth() - 7;
					double y1 = (arrow.startY+1) * (OutputUI.preferredSize.getHeight() + 4) - 8;
					double x2 = (arrow.endX - 1) * (OutputUI.preferredSize.getWidth() + 8 + OutputUI.gapSize.getWidth()) + 3;
					double y2 = arrow.endY * (OutputUI.preferredSize.getHeight() + 4) +3;
					Color c = ColourStore.getColor(arrow.dep.getDependentRegister());
					toFill.surface.addStar(x1, y1, x2, y2, c, arrow.dep.getType());
				}
			}
		}
		toFill.surface.repaint();
		toFill.surface.revalidate();
	}

	public boolean isStalled(String str)
	{
		if (str == null)
			return false;
		return str.startsWith("@") || str.equals("...");
	}
}
