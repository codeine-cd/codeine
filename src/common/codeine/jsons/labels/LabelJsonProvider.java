package codeine.jsons.labels;

import java.util.Set;

public interface LabelJsonProvider {

	Set<ProjectLabelVersionJson> versions(String project);

	void updateLabel(ProjectLabelVersionJson versionLabelJson);

	void deleteLabel(String label, String project);

	/**
	 * @return label for version (version if label not found)
	 */
	String labelForVersion(String version, String project);

	/**
	 * @return version for label (label if version not found)
	 */
	String versionForLabel(String label, String project);

}