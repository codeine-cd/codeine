package codeine.jsons.nodes;

import codeine.jsons.project.ProjectJson;



public interface NodesManager {
	
	NodeListJson nodesOf(ProjectJson projectJson);
	void init(ProjectJson projectJson);
}
