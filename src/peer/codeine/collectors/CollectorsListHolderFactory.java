package codeine.collectors;

import codeine.api.NodeInfo;

public interface CollectorsListHolderFactory {

	public CollectorsListHolder create(String projectName, NodeInfo node);
}
