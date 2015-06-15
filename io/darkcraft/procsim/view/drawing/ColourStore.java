package io.darkcraft.procsim.view.drawing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColourStore
{
	private static int[] colorMap = new int[]{

									0xC00000, // Red
	                                0xD2B300, // Vivid Yellow
	                                0x3366FF, // Light blue
	                                0xC13EB5, // Strong Purple
	                                0xFF6800, // Vivid Orange
	                             //   0xA6BDD7, // Very Light Blue
	                             //   0xCEA262, // Grayish Yellow

	                                // The following don't work well for people with defective color vision
	                                0x007D34, // Vivid Green
	                                0xF6768E, // Strong Purplish Pink
	                                0x00538A, // Strong Blue
	                                0xFF7A5C, // Strong Yellowish Pink
	                                0x53377A, // Strong Violet
	                                0xFF8E00, // Vivid Orange Yellow
	                                0xB32851, // Strong Purplish Red
	                                0xF4C800, // Vivid Greenish Yellow
	                                0x7F180D, // Strong Reddish Brown
	                                0x93AA00, // Vivid Yellowish Green
	                                0x593315, // Deep Yellowish Brown
	                                0xF13A13, // Vivid Reddish Orange
	                                0x232C16};// Dark Olive Green
	private static List<Color> colors;
	private static Map<String,Integer> strMap = new HashMap<String,Integer>();

	static
	{
		colors = new ArrayList<Color>();
		colors.add(Color.BLACK);
		for(int i : colorMap)
		{
			int r = (i & 0xFF0000) >> 16;
			int g = (i & 0x00FF00) >> 8;
			int b = (i & 0x0000FF);
			float[] hsb = Color.RGBtoHSB(r, g, b, null);
			colors.add(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
		}
	}

	private static Color getColor(int i)
	{
		return colors.get(i % colors.size());
	}

	public static Color getColor(String str)
	{
		if(strMap.containsKey(str))
			return getColor(strMap.get(str));
		int s = strMap.size();
		strMap.put(str, s);
		return getColor(s);
	}
}
