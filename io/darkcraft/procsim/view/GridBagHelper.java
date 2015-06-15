package io.darkcraft.procsim.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

public class GridBagHelper
{
	private static Insets defaultInsets = new Insets(2,2,2,2);
	public static GridBagConstraints getConstraints()
	{
		GridBagConstraints c = new GridBagConstraints();
		c.insets = defaultInsets;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		return c;
	}

	public static GridBagConstraints getConstraints(int x, int y)
	{
		GridBagConstraints c = getConstraints();
		c.gridx = x;
		c.gridy = y;
		return c;
	}

	public static GridBagConstraints setWeights(double w, GridBagConstraints c)
	{
		c.weightx = w;
		c.weighty = w;
		return c;
	}

	public static GridBagConstraints setWeights(double wx, double wy, GridBagConstraints c)
	{
		c.weightx = wx;
		c.weighty = wy;
		return c;
	}

	public static GridBagConstraints getConstraints(int x, int y, int sx, int sy)
	{
		GridBagConstraints c = getConstraints(x,y);
		c.gridwidth = sx;
		c.gridheight = sy;
		return c;
	}

	public static Object getConstraints(int x, int y, int sx, int sy, int anchor)
	{
		GridBagConstraints c = getConstraints(x,y,sx,sy);
		c.anchor=anchor;
		return c;
	}

	public static LayoutManager getLayout()
	{
		GridBagLayout layout = new GridBagLayout();
		return layout;
	}
}
