package codeine.collectors;

import codeine.api.NodeInfo;

public interface CollectorsRunnerFactory {

	public CollectorsRunner create(String projectName, NodeInfo node);
}
