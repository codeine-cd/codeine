package codeine.jsons.project;

@SuppressWarnings("unused")
public class CodeineProject {

	private String name;
	private String description;
	private int nodes_count;
	
	public CodeineProject(String name, int nodes_count, String description) {
		super();
		this.name = name;
		this.description = description;
		this.nodes_count = nodes_count;
	}

	public String name() {
		return name;
	}
	
	
}
