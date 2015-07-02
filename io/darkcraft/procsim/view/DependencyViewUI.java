package io.darkcraft.procsim.view;

import io.darkcraft.procsim.controller.DependencyType;
import io.darkcraft.procsim.model.dependencies.IDependency;
import io.darkcraft.procsim.model.helper.MapList;
import io.darkcraft.procsim.model.helper.Pair;
import io.darkcraft.procsim.model.instruction.IInstruction;
import io.darkcraft.procsim.model.simulator.AbstractSimulator;
import io.darkcraft.procsim.view.drawing.DrawingSurface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DependencyViewUI
{
	private final AbstractSimulator		sim;
	private final List<IInstruction>	instructions;
	private final List<IDependency>		dependencies;

	private JFrame						mainFrame;
	private JScrollPane					scroll;
	private JLayeredPane				layered;
	private JPanel						pane;
	private DrawingSurface				surface;

	public DependencyViewUI(AbstractSimulator _sim, List<IInstruction> _inst, List<IDependency> _deps)
	{
		sim = _sim;
		instructions = _inst;
		dependencies = _deps;
		mainFrame = new JFrame();
		mainFrame.setLayout(GridBagHelper.getLayout());
		layered = new JLayeredPane();
		layered.setLayout(GridBagHelper.getLayout());
		surface = new DrawingSurface();
		pane = new JPanel();
		pane.setLayout(GridBagHelper.getLayout());
		layered.setLayer(surface, 1, 2);
		layered.setLayer(pane, 0, 0);
		layered.add(surface, GridBagHelper.getConstraints(0, 0));
		layered.add(pane, GridBagHelper.getConstraints(0, 0));
		scroll = new JScrollPane(layered);
		scroll.setMaximumSize(new Dimension(540,600));
		mainFrame.add(scroll, GridBagHelper.setWeights(1,GridBagHelper.getConstraints(0,1,4,4)));
		mainFrame.setTitle("ProcSim - Data Dependencies");
		fillData();
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private boolean shiftedRight(int row)
	{
		return (row % 2) == 0;
	}

	private Insets insets = new Insets(2,2,35,25);
	private Dimension half = null;
	private void fillData()
	{
		pane.removeAll();
		surface.clear();
		Map<IInstruction,Pair<Integer,Integer>> posMap = new HashMap();
		MapList<Integer,IInstruction> heightMap = new MapList();
		Map<IInstruction, JLabel> labelMap = new HashMap();
		for(IInstruction inst : instructions)
		{
			int highest = 0;
			for(IDependency dep : dependencies)
			{
				if(dep.getTo().equals(inst))
				{
					IInstruction from = dep.getFrom();
					if(posMap.containsKey(from))
					{
						Pair<Integer,Integer> pos = posMap.get(from);
						if(pos.b > highest)
							highest = pos.b;
					}
				}
			}
			posMap.put(inst, new Pair<Integer,Integer>(0,highest+1));
			heightMap.add(highest+1, inst);
		}
		for(Integer i : heightMap.keySet())
		{
			int x = 0;
			for(IInstruction inst : heightMap.getList(i))
			{
				posMap.put(inst, new Pair<Integer,Integer>(x++,i));
			}
		}
		for(Integer i : heightMap.keySet())
		{
			if(shiftedRight(i))
			{
				if(half == null)
				{
					Dimension normal = LabelHelper.getSize();
					half = new Dimension(normal.width/2, normal.height);
				}
				pane.add(LabelHelper.get("",half), GridBagHelper.getConstraints(0,i,1,1));
			}
			else
			{
				if(half == null)
				{
					Dimension normal = LabelHelper.getSize();
					half = new Dimension(normal.width/2, normal.height);
				}
				int size = heightMap.size(i);
				pane.add(LabelHelper.get("",half), GridBagHelper.getConstraints((size*2),i,1,1));
			}
		}
		for(IInstruction inst : instructions)
		{
			Pair<Integer,Integer> pos = posMap.get(inst);
			JLabel l = LabelHelper.get(inst.toString());
			int x = (pos.a * 2) + (shiftedRight(pos.b) ? 1 : 0);
			int y = pos.b;
			pane.add(l, GridBagHelper.setInsets(insets, GridBagHelper.getConstraints(x, y, 2, 1)));
			labelMap.put(inst, l);
		}
		layered.setPreferredSize(pane.getPreferredSize());
		pane.revalidate();
		mainFrame.pack();
		for(IInstruction from : instructions)
		{
			int max = 0;
			for(IDependency d : dependencies)
			{
				if(from != d.getFrom()) continue;
				max++;
			}
			int i = 1;
			for(IDependency d : dependencies)
			{
				if(from != d.getFrom()) continue;
				IInstruction to = d.getTo();
				JLabel fromLabel = labelMap.get(from);
				JLabel toLabel = labelMap.get(to);
				double x1 = fromLabel.getX() + ((fromLabel.getSize().getWidth() * i++)/(max+1));
				double y1 = fromLabel.getY() + LabelHelper.getSize().getHeight();
				double x2 = toLabel.getX() + (toLabel.getSize().getWidth()/2);
				double y2 = toLabel.getY();
				Color c;
				DependencyType dt = d.getType();
				switch(dt)
				{
					case RAW : c = Color.RED; break;
					case WAW : c = Color.BLUE; break;
					case WAR : c = Color.GREEN; break;
					default: c = Color.BLACK;
				}
				surface.addArrow(x1, y1, x2, y2, c, d.getType());
			}
		}
		surface.revalidate();
		pane.revalidate();
	}

	public boolean isVisible()
	{
		return mainFrame.isVisible();
	}
}
