package io.darkcraft.procsim.view.drawing;

import io.darkcraft.procsim.controller.DependencyType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class DrawingSurface extends JPanel implements ActionListener
{
	private static final long		serialVersionUID	= 1561375925483499404L;
	private ArrayList<ArrowStore>	arrows				= new ArrayList<ArrowStore>();
	private ArrayList<ArrowStore>	stars				= new ArrayList<ArrowStore>();

	{
		setOpaque(false);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		doDrawing(g);
	}

	public void clear()
	{
		arrows.clear();
		stars.clear();
	}

	private void doDrawing(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		g.setStroke(new BasicStroke(0.8f));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(Color.BLACK);
		for(ArrowStore a : arrows)
			drawArrow(g, a, 5);
		for(ArrowStore a : stars)
			drawStar(g,a,4);
	}

	private void drawArrow(Graphics2D g, ArrowStore a, int barb)
	{
		double x1 = a.x1;
		double y1 = a.y1;
		double x2 = a.x2;
		double y2 = a.y2;
		g.setPaint(a.c);
		DependencyType d = a.t;

		g.draw(new Line2D.Double(x1, y1, x2, y2));
		double phi = Math.PI / 4;
		double theta = Math.atan2(y2 - y1, x2 - x1);

		double xMid = (a.x1 + a.x2) / 2;
		double yMid = (a.y1 + a.y2) / 2;
		if(d == DependencyType.WAW)
			g.draw(new Ellipse2D.Double(xMid - (barb/2),yMid - (barb/2), barb, barb));
		else if(d == DependencyType.WAR)
		{
			double perp = Math.PI/2;
			double xs = xMid - barb * Math.cos(theta + perp);
			double ys = yMid - barb * Math.sin(theta + perp);
			double x = xMid - (barb/2) * Math.cos(theta - perp);
			double y = yMid - (barb/2) * Math.sin(theta - perp);
			g.draw(new Line2D.Double(x,y,xs,ys));
		}

		double x = x2 - barb * Math.cos(theta + phi);
		double y = y2 - barb * Math.sin(theta + phi);
		g.draw(new Line2D.Double(x2, y2, x, y));
		x = x2 - barb * Math.cos(theta - phi);
		y = y2 - barb * Math.sin(theta - phi);
		g.draw(new Line2D.Double(x2, y2, x, y));
		g.setPaint(Color.BLACK);
	}

	private void drawStar(Graphics2D g, ArrowStore a, int barb)
	{
		g.setPaint(a.c);
		DependencyType d = a.t;
		int spokes;
		switch(d)
		{
		case RAW: spokes = 6; break;
		case WAR: spokes = 8; break;
		case WAW: spokes = 5; break;
		default: spokes = 4;
		}
		drawStar(g, a.x1, a.y1, spokes, barb);
		drawStar(g, a.x2, a.y2, spokes, barb);
		g.setPaint(Color.BLACK);
	}

	private void drawStar(Graphics2D g, double x, double y, int spokes, int barb)
	{
		double rot = (2 * Math.PI) / spokes;
		for(int i = 0; i < spokes; i++)
		{
			double angle = rot * i;
			double x1 = x + barb * Math.sin(angle);
			double y1 = y + barb * Math.cos(angle);
			g.draw(new Line2D.Double(x1,y1,x,y));
		}
	}

	public void addArrow(double x1, double y1, double x2, double y2)
	{
		arrows.add(new ArrowStore(x1,y1,x2,y2,Color.BLACK, DependencyType.RAW));
	}

	public void addArrow(double x1, double y1, double x2, double y2, DependencyType a)
	{
		arrows.add(new ArrowStore(x1,y1,x2,y2,Color.BLACK, a));
	}

	public void addArrow(double x1, double y1, double x2, double y2, Color c)
	{
		arrows.add(new ArrowStore(x1,y1,x2,y2,c,DependencyType.RAW));
	}

	public void addArrow(double x1, double y1, double x2, double y2, Color c, DependencyType a)
	{
		arrows.add(new ArrowStore(x1,y1,x2,y2,c,a));
	}

	public void addStar(double x1, double y1, double x2, double y2, Color c, DependencyType a)
	{
		stars.add(new ArrowStore(x1,y1,x2,y2,c,a));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		repaint();
	}

	private class ArrowStore
	{
		public final double	x1, y1, x2, y2;
		public final Color c;
		public final DependencyType t;

		public ArrowStore(double _x1, double _y1, double _x2, double _y2, Color _c, DependencyType _d)
		{
			x1	= _x1;
			y1	= _y1;
			x2	= _x2;
			y2	= _y2;
			c	= _c;
			t = _d;
		}
	}

	@Override
	public void removeAll()
	{
		super.removeAll();
		arrows.clear();
	}
}
