package codeine.servlets.front_end;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProjectsTab {

	
	private String name;
	private List<String> exp;

	public ProjectsTab(String name, List<String> exp) {
		this.name = name;
		this.exp = exp;
		
	}
	
	public String name() {
		return name;
	}
	
	public Collection<String> exp() {
		return Collections.unmodifiableCollection(exp);
	}

	@Override
	public String toString() {
		return "ProjectsTab [name=" + name + ", exp=" + exp + "]";
	}
	
	
}
