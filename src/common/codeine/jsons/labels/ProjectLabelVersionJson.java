package codeine.jsons.labels;

public class ProjectLabelVersionJson {
	
	private String version;
	private String label;
	private String description;
	private String project;
	
	public String label(){
		return (null == label) ? version : label;
	}
	public String description(){
		return description;
	}
	public String name() {
		return version;
	}

	@Override
	public String toString() {
		return "ProjectLabelVersionJson [version=" + version + ", label=" + label + ", description=" + description
				+ ", project=" + project + "]";
	}
	public String project() {
		return project;
	}
	public String version() {
		return version;
	}
}
