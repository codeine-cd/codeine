package codeine.servlets.template;

import java.util.List;

import codeine.api.NodeDataJson;

import com.google.common.collect.Lists;

@SuppressWarnings("unused")
public class VersionNodesJson {
	
	private String version;
	private String id;
	private List<NodeDataJson> node = Lists.newArrayList();
	private Integer count;
	
	public VersionNodesJson(String version) {
		this.version = version;
		setId();
	}

	public void setId() {
		id = version.replace('.', '_').replace(' ', '_');
	}
	
	public void updateCount(){
		count = node.size();
	}
	public List<NodeDataJson> node() {
		return node;
	}
}
