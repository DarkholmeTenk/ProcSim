package io.darkcraft.procsim.controller;

import io.darkcraft.procsim.model.dependencies.IDependency;

import java.util.Comparator;

public class DependencyComparator implements Comparator<IDependency>
{

	@Override
	public int compare(IDependency a, IDependency b)
	{
		if (a == null && b == null)
			return 0;
		if (a == null)
			return 1;
		if (b == null)
			return -1;
		return Integer.compare(a.getFrom().getStartTime(), b.getFrom().getStartTime());
	}

}
