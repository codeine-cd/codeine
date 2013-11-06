package codeine.jsons.labels;

import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import codeine.configuration.PathHelper;
import codeine.utils.JsonFileUtils;

public class LabelJsonFromFileProvider implements LabelJsonProvider {

	private @Inject JsonFileUtils jsonFileUtils;
	private @Inject PathHelper pathHelper;
	
	@Override
	public Set<ProjectLabelVersionJson> versions(String project) {
		return jsonFileUtils.getConfFromFile(pathHelper.getVersionLabelFile(project), new VersionLabelSetJson()).versions();
	}
	
	private void store(String projectName, VersionLabelSetJson versionLabelSetJson) {
		jsonFileUtils.setContent(pathHelper.getVersionLabelFile(projectName), versionLabelSetJson);
	}

	@Override
	public void updateLabel(ProjectLabelVersionJson versionLabelJson) {
		deleteLabel(versionLabelJson.label(), versionLabelJson.project());
		addLabel(versionLabelJson, versionLabelJson.project());
	}

	private void addLabel(ProjectLabelVersionJson versionLabelJson, String projectName) {
		Set<ProjectLabelVersionJson> versions = versions(projectName);
		versions.add(versionLabelJson);
		VersionLabelSetJson versionLabelSetJson = new VersionLabelSetJson(versions);
		store(projectName, versionLabelSetJson);
	}

	@Override
	public void deleteLabel(String alias, String projectName) {
		Set<ProjectLabelVersionJson> versions2 = versions(projectName);
		for (Iterator<ProjectLabelVersionJson> iterator = versions2.iterator(); iterator.hasNext();) {
			ProjectLabelVersionJson versionLabelJson2 = (ProjectLabelVersionJson) iterator.next();
			if (versionLabelJson2.label().equals(alias)){
				iterator.remove();
			}
		}
		VersionLabelSetJson versionLabelSetJson = new VersionLabelSetJson(versions2);
		store(projectName, versionLabelSetJson);
	}

	@Override
	public String labelForVersion(String version, String projectName) {
		Set<ProjectLabelVersionJson> versions = versions(projectName);
		for (ProjectLabelVersionJson versionLabelJson : versions) {
			if (versionLabelJson.name().equals(version)){
				return versionLabelJson.label();
			}
		}
		return version;
	}
	@Override
	public String versionForLabel(String label, String projectName) {
		Set<ProjectLabelVersionJson> versions = versions(projectName);
		for (ProjectLabelVersionJson versionLabelJson : versions) {
			if (versionLabelJson.label().equals(label)){
				return versionLabelJson.name();
			}
		}
		return label;
	}
}
