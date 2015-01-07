package codeine.collectors.version;

import codeine.api.NodeInfo;

public interface VersionCollectorRunnerFactory {

	public VersionCollectorRunner create(String name, NodeInfo node);
}
