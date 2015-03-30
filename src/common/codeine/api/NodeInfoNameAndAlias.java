package codeine.api;

public class NodeInfoNameAndAlias {

	private String name;
	private String alias;

	public NodeInfoNameAndAlias() {

	}

	public NodeInfoNameAndAlias(String name) {
		this(name, null);
	}
	
	public NodeInfoNameAndAlias(String name, String alias) {
		this.name = name;
		this.alias = alias == null ? name : alias;
	}
	
	public NodeInfoNameAndAlias(NodeInfoNameAndAlias node) {
		this(node.name, node.alias);
	}

	public String alias() {
		return null == alias ? name : alias;
	}

	@Override
	public String toString() {
		return "NodeInfo [name=" + name + ", alias=" + alias + "]";
	}
	
	public String name() {
		return name;
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
		NodeInfoNameAndAlias other = (NodeInfoNameAndAlias) obj;
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
		return true;
	}
	
	
}
