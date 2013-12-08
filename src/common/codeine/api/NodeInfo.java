package codeine.api;

public class NodeInfo {

	private String name;
	private String alias;

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
		return "NodeJson [name=" + name + ", alias=" + alias + "]";
	}

	public String name() {
		return name;
	}

	
}
