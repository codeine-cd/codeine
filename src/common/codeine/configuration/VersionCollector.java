package codeine.configuration;

import codeine.model.Constants;

import com.google.common.collect.Lists;

public class VersionCollector extends HttpCollector
{
	
	public VersionCollector()
	{
		super("version", Lists.newArrayList("all"), Lists.newArrayList(new CollectorRule(null, "all", Lists.newArrayList(Constants.REPLACE_NODE_NAME))), false);
	}
	
}
