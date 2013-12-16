package codeine.configuration;

import java.util.List;

import codeine.jsons.project.ProjectJson;

public class ConfiguredProjectUtils {

	public List<NodeMonitor> dependsOn(NodeMonitor collector, ProjectJson p)
	{
		throw new UnsupportedOperationException();
//		final List<HttpCollector> l = Lists.newLinkedList();
//		for (HttpCollector c : p.collectors)
//		{
//			for (String name1 : collector.dependsOn)
//			{
//				if (name1.equals(c.name))
//				{
//					l.add(c);
//				}
//			}
//		}
//		return l;
	}
}
