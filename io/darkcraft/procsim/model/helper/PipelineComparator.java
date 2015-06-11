package io.darkcraft.procsim.model.helper;

import io.darkcraft.procsim.model.components.abstracts.AbstractPipeline;

import java.util.Comparator;

public class PipelineComparator implements Comparator<AbstractPipeline>
{
	private final int stage;
	public PipelineComparator(int _stage)
	{
		stage = _stage;
	}
	@Override
	public int compare(AbstractPipeline o1, AbstractPipeline o2)
	{
		int a = o1.getInstructionTime(stage);
		int b = o2.getInstructionTime(stage);
		return Integer.compare(a, b);
	}
}
