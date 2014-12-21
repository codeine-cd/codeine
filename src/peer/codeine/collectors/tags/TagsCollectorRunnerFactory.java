package codeine.collectors.tags;

import codeine.api.NodeInfo;
import codeine.jsons.project.ProjectJson;

public interface TagsCollectorRunnerFactory {

	public TagsCollectorRunner create(ProjectJson project, NodeInfo node);
}
