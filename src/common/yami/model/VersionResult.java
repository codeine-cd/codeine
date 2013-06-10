package yami.model;

import yami.configuration.Node;
import yami.configuration.VersionCollector;
import yami.mail.CollectorOnNodeState;

public class VersionResult
{
	public static final String NO_VERSION = "No version";
	
	public static String getVersion(DataStore d, Node node)
	{
		VersionCollector c = new VersionCollector();
		CollectorOnNodeState result = d.getResult(node, c);
		if (null == result)
		{
			return NO_VERSION;
		}
		Result last = result.getLast();
		if (null == last)
		{
			return NO_VERSION;
		}
		String[] split = last.output.split("\n");
		return split[split.length - 1];
	}
}
