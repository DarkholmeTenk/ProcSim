package io.darkcraft.procsim.view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

public class LabelHelper
{
	public static final Font boldFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
	public static final Font plainFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	public static JLabel get(String s)
	{
		return get(s, null, false, boldFont);
	}

	public static JLabel getPlain(String s)
	{
		return get(s, null, false, plainFont);
	}

	public static JLabel get(String s, Dimension d)
	{
		return get(s,d,false,boldFont);
	}

	public static JLabel getPlain(String s, Dimension d)
	{
		return get(s,d,false,plainFont);
	}

	public static JLabel get(String s, boolean op)
	{
		return get(s,null,op,boldFont);
	}

	public static JLabel getPlain(String s, boolean op)
	{
		return get(s,null,op,plainFont);
	}

	public static JLabel get(String s, Dimension d, boolean op)
	{
		return get(s,d,op,boldFont);
	}

	public static JLabel getPlain(String s, Dimension d, boolean op)
	{
		return get(s,d,op,plainFont);
	}

	public static JLabel get(String s, Dimension d, boolean op, Font f)
	{
		JLabel l = new JLabel(s);
		if(d != null)
			l.setPreferredSize(d);
		l.setOpaque(op);
		l.setFont(f);
		return l;
	}
}
