package codeine.collectors.version;

import codeine.api.NodeInfo;
import codeine.jsons.project.ProjectJson;

public interface VersionCollectorRunnerFactory {

	public VersionCollectorRunner create(ProjectJson project, NodeInfo node);
}
