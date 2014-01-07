package codeine.exceptions;

public class ProjectNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String projectName;

	public ProjectNotFoundException(String projectName) {
		super();
		this.projectName = projectName;
	}
	
	@Override
	public String getMessage() {
		return "Project '" + projectName + "' not found";
	}
	
}
