package codeine.configuration;

import java.util.List;

import javax.inject.Inject;

import codeine.api.NodeInfo;
import codeine.jsons.nodes.NodesManager;
import codeine.jsons.project.ProjectJson;
import codeine.utils.network.InetUtils;

import com.google.common.collect.Lists;

public class MyDummyConfiguration implements IConfigurationManager {

	private ProjectJson $ = new ProjectJson("test_project");
	@Inject private NodesManager nodesManager;
	public MyDummyConfiguration() {
		super();
		List<NodeInfo> nodes = Lists.newArrayList();
		NodeInfo n = new NodeInfo(InetUtils.getLocalHost().getHostName());
		nodes.add(n );
		$.nodes_info(nodes);
	}
	
	@Override
	public void refresh() {
		nodesManager.init($);
	}


	@Override
	public ProjectJson getProjectForName(String projectName) {
		if (projectName.equals("test_project")) {
			return $ ;
		}
		return null;
	}

	@Override
	public List<ProjectJson> getConfiguredProjects() {
		return Lists.newArrayList($);
	}

	@Override
	public boolean hasProject(String project) {
		return project.equals("test_project");
	}

}
