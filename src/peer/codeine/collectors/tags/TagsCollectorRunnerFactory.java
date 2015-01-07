package codeine.collectors.tags;

import codeine.api.NodeInfo;

public interface TagsCollectorRunnerFactory {

	public TagsCollectorRunner create(String name, NodeInfo node);
}
