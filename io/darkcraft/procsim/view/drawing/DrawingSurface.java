package io.darkcraft.procsim.view.drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class DrawingSurface extends JPanel implements ActionListener
{
	private static final long		serialVersionUID	= 1561375925483499404L;
	private ArrayList<ArrowStore>	arrows				= new ArrayList<ArrowStore>();

	{
		setOpaque(false);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		doDrawing(g);
	}

	private void doDrawing(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		g.setStroke(new BasicStroke(1.2f));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(Color.BLACK);
		for(ArrowStore a : arrows)
			drawArrow(g, a.x1, a.y1, a.x2, a.y2, 5);
	}

	private void drawArrow(Graphics2D g, double x1, double y1, double x2, double y2, int barb)
	{
		g.draw(new Line2D.Double(x1, y1, x2, y2));
		double phi = Math.PI / 4;
		double theta = Math.atan2(y2 - y1, x2 - x1);
		double x = x2 - barb * Math.cos(theta + phi);
		double y = y2 - barb * Math.sin(theta + phi);
		g.draw(new Line2D.Double(x2, y2, x, y));
		x = x2 - barb * Math.cos(theta - phi);
		y = y2 - barb * Math.sin(theta - phi);
		g.draw(new Line2D.Double(x2, y2, x, y));
	}

	public void addArrow(double x1, double y1, double x2, double y2)
	{
		arrows.add(new ArrowStore(x1,y1,x2,y2));
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		repaint();
	}

	private class ArrowStore
	{
		public final double	x1, y1, x2, y2;

		public ArrowStore(double _x1, double _y1, double _x2, double _y2)
		{
			x1 = _x1;
			y1 = _y1;
			x2 = _x2;
			y2 = _y2;
		}
	}

	@Override
	public void removeAll()
	{
		super.removeAll();
		arrows.clear();
	}
}
