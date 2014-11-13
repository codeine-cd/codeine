package codeine.collectors;

import codeine.api.NodeInfo;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.project.ProjectJson;

public interface OneCollectorRunnerFactory {

	public OneCollectorRunner create(CollectorInfo collector, ProjectJson project, NodeInfo node);
}
