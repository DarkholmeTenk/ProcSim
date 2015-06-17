package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.helper.MapList;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class OutputController
{
	private OutputUI											toFill;
	private AbstractSimulator									simulator;
	private List<List<Pair<IInstruction, String>>>				fillData	= null;
	private List<IInstruction>									insts		= null;
	private Map<Pair<Integer, Integer>, List<ArrowDataStore>>	links		= new HashMap();
	private Comparator<IDependency>								depComp		= new DependencyComparator();
	private MapList<String,JLabel>								registerLabelMap = new MapList();

	private static final Color bgColor1 = Color.getHSBColor(0, 0, 0.9f);
	private static final Color bgColor2 = Color.LIGHT_GRAY;
	private static final Color fg = Color.BLACK;

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
		boolean green = str.startsWith(";");
		boolean red = str.startsWith("*");
		boolean purp = str.startsWith("@");
		//str = str.replace("#", "").replace("*", "").replace("@", "").replaceAll("(#|*|@|\\?)", "");
		str = str.replaceAll("(\\;|\\*|@|\\?)", "");
		JLabel label = new JLabel(str);
		label.setForeground(fg);
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
			label.setBackground(bgColor1);
		else
			label.setBackground(bgColor2);
		if (j > 0)
			toFill.dataPanel.add(label, GridBagHelper.getConstraints(2 * j, i));
		else
		{
			String[] data = str.split(" ");
			if(data.length > 1)
				for(int x = 0; x < data.length; x++)
				{
					String temp = data[x];
					label = new JLabel(temp);
					label.setForeground(fg);
					if (i % 2 == 0)
						label.setBackground(bgColor1);
					else
						label.setBackground(bgColor2);
					if(x > 1)
					{
						if(!temp.startsWith("#"))
							registerLabelMap.add(temp, label);
					}
					label.setOpaque(true);
					toFill.instructionPanel.add(label, GridBagHelper.getConstraints(x,i));
				}
			else
				toFill.instructionPanel.add(label, GridBagHelper.getConstraints(j, i, 4, 1));
		}
	}

	private void color(String register, Color c)
	{
		for(JLabel l : registerLabelMap.getList(register))
			l.setForeground(c);
	}

	private Dimension minimal = new Dimension(0,0);
	public void fillResults(int upTo)
	{
		if (insts == null)
			insts = simulator.getInstructions();
		Map<IInstruction, List<IDependency>> depMap = DependencyGraphBuilder.getToDependencies(insts);
		if (fillData == null)
			fillData = OutputHelper.outputData(simulator);
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
				if (str.startsWith("?"))
					fillArrows(insts, depMap, fillData, i, j);
			}

		}
		JLabel gap = new JLabel(" ");
		gap.setPreferredSize(OutputUI.gapSize);
		toFill.dataPanel.add(gap, GridBagHelper.setWeights(1, 0, GridBagHelper.getConstraints(2 * upTo + 1, 0)));
		if (upTo < fillData.get(0).size() - 1)
		{
			JLabel l = new JLabel("");
			l.setPreferredSize(minimal);
			toFill.dataPanel.add(l, GridBagHelper.setWeights(0, 1, GridBagHelper.getConstraints(2 * upTo + 1, lowestY + 1)));
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
		List<IDependency> deps = originalDeps;
		Collections.sort(deps, depComp);
		Pair pair = new Pair(j, i);
		if (!links.containsKey(pair))
			links.put(pair, new ArrayList());
		else
			return;
		List<ArrowDataStore> arrows = links.get(pair);
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
					Pair<IInstruction, String> pair1 = row.get(x);
					Pair<IInstruction, String> pair2 = tempRow.get(x);
					if ((pair1 != null && !isStalled(pair1.b)) && x != j)
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
				arrows.add(new ArrowDataStore(finishTime, rowNum, j, i, d, count, 1));
			}
		}
	}

	public void addArrowsToSurface()
	{
		toFill.surface.clear();
		if (toFill.dependencyDisplay == 1)
		{
			for (Pair<Integer, Integer> i : links.keySet())
			{
				if (i.a > toFill.stateNum)
					continue;
				List<ArrowDataStore> arrows = links.get(i);
				int count = 1;
				for (ArrowDataStore arrow : arrows)
				{
					double yO = (count++ / (double) (arrows.size() + 1)) * OutputUI.preferredSize.getHeight();
					double x1 = arrow.startX * (OutputUI.preferredSize.getWidth() + 8 + OutputUI.gapSize.getWidth()) - OutputUI.gapSize.getWidth() - 7;
					double y1 = (arrow.startY + 1) * (OutputUI.preferredSize.getHeight() + 4) - 8;
					double x2 = (arrow.endX - 1) * (OutputUI.preferredSize.getWidth() + 8 + OutputUI.gapSize.getWidth()) + 3;
					double y2 = arrow.endY * (OutputUI.preferredSize.getHeight() + 4) + 3 + yO;
					Color c = ColourStore.getColor(arrow.dep.getDependentRegister());
					toFill.surface.addArrow(x1, y1, x2, y2, c, arrow.dep.getType());
				}
			}
		}
		else if (toFill.dependencyDisplay == 2)
		{

			for (Pair<Integer, Integer> i : links.keySet())
			{
				List<ArrowDataStore> arrows = links.get(i);
				int count = 1;
				for (ArrowDataStore arrow : arrows)
				{
					int sX = arrow.startX;
					int sY = arrow.startY;
					int eX = arrow.endX;
					int eY = arrow.endY;
					if(eX > toFill.stateNum)
					{
						if(toFill.stateNum <= 1) continue;
						String desiredState = fillData.get(eY).get(eX-1).b;
						String currentState = fillData.get(eY).get(toFill.stateNum).b;
						if(!desiredState.equals(currentState))
						{
							if(currentState.equals("..."))
							{
								if(!desiredState.equals(fillData.get(eY).get(toFill.stateNum-1).b))
									continue;
							}
							else
								continue;
						}
						eX = sX = toFill.stateNum;
					}
					double yO = (count++ / (double) (arrows.size() + 1)) * OutputUI.preferredSize.getHeight();
					double x1 = sX * (OutputUI.preferredSize.getWidth() + 8 + OutputUI.gapSize.getWidth()) - OutputUI.gapSize.getWidth() - 7;
					double y1 = (sY + 1) * (OutputUI.preferredSize.getHeight() + 4) - 8;
					double x2 = (eX - 1) * (OutputUI.preferredSize.getWidth() + 8 + OutputUI.gapSize.getWidth()) + 3;
					double y2 = eY * (OutputUI.preferredSize.getHeight() + 4) + 3 + yO;
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

	public void clear()
	{

	}

	public int getMaxStateNum()
	{
		return fillData.get(0).size() - 1;
	}
}
