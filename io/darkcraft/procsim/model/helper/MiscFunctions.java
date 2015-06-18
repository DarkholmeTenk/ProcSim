package io.darkcraft.procsim.model.helper;

public class MiscFunctions
{
	public static boolean in(int[] a, int b)
	{
		for(int i : a)
			if(i == b)
				return true;
		return false;
	}

	public static int in(int[][] a, int b)
	{
		for(int i = 0; i < a.length; i++)
		{
			int[] ar = a[i];
			if(in(ar,b))
				return i;
		}
		return -1;
	}

	public static int min(int[] a)
	{
		int min = Integer.MAX_VALUE;
		for(int i : a)
			if(i < min)
				min = i;
		return min;
	}

	public static int min(int[][] a)
	{
		int min = Integer.MAX_VALUE;
		for(int[] i : a)
		{
			int m = min(i);
			if(m < min)
				min = m;
		}
		return min;
	}

	public static int max(int[] a)
	{
		int max = Integer.MIN_VALUE;
		for(int i : a)
			if(i > max)
				max = i;
		return max;
	}

	public static int max(int[][] a)
	{
		int max = Integer.MIN_VALUE;
		for(int[] i : a)
		{
			int m = min(i);
			if(m > max)
				max = m;
		}
		return max;
	}
}
