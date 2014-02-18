package codeine.api;

import java.util.List;

import com.google.common.collect.Lists;


public class NodeInfo {

	private String name;
	private String alias;
	private List<String> tags = Lists.newArrayList();

	public NodeInfo(){
		
	}
	public NodeInfo(String name) {
		this.name = name;
	}

	public NodeInfo(String name, String alias) {
		this.name = name;
		this.alias = alias == null ? name : alias;
	}

	public String alias() {
		return null == alias ? name : alias;
	}

	@Override
	public String toString() {
		return "NodeInfo [name=" + name + ", alias=" + alias + "]";
	}

	public List<String> tags() {
		if (null == tags) {
			return Lists.newArrayList();
		}
		return tags;
	}
	
	public String name() {
		return name;
	}

	public List<String> tags(List<String> tags) {
		List<String> $ = this.tags;
		this.tags = tags;
		return $;
	}	
	

	public String node_alias() {
		return alias;
	}
}
