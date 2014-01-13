package codeine.api;

import java.util.List;


public class NodeInfo {

	private String name;
	private String alias;
	private List<String> tags;

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
}
