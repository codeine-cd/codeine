package codeine.jsons.labels;

import java.util.Set;


import com.google.common.collect.Sets;

public class VersionLabelSetJson {
	private Set<ProjectLabelVersionJson> versions = Sets.newHashSet();

	
	public VersionLabelSetJson() {
		super();
	}

	public VersionLabelSetJson(Set<ProjectLabelVersionJson> versions) {
		this.versions = versions;
	}

	public Set<ProjectLabelVersionJson> versions() {
		return versions;
	}
}
