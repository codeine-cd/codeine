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

	public NodeInfo(String name, String alias, List<String> tags) {
		super();
		this.name = name;
		this.alias = alias;
		this.tags = tags;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeInfo other = (NodeInfo) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}
	
	
}
