package codeine.jsons.labels;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import codeine.configuration.PathHelper;
import codeine.model.Constants;
import codeine.utils.JsonFileUtils;

import com.google.common.collect.Maps;

public class LabelJsonFromFileProvider implements LabelJsonProvider {

	private @Inject JsonFileUtils jsonFileUtils;
	private @Inject PathHelper pathHelper;
	private Map<String, Set<ProjectLabelVersionJson>> labels = Maps.newConcurrentMap();
	
	private Set<ProjectLabelVersionJson> forProject(String project){
		if (!labels.containsKey(project)){
			Set<ProjectLabelVersionJson> versions = Collections.newSetFromMap(new ConcurrentHashMap<ProjectLabelVersionJson, Boolean>());
			versions.addAll(jsonFileUtils.getConfFromFile(pathHelper.getVersionLabelFile(project), new VersionLabelSetJson()).versions());
			labels.put(project, versions);
		}
		return labels.get(project);
	}
	
	@Override
	public Set<ProjectLabelVersionJson> versions(String project) {
		return forProject(project); 
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
		synchronized (versions2) {
			for (Iterator<ProjectLabelVersionJson> iterator = versions2.iterator(); iterator.hasNext();) {
				ProjectLabelVersionJson versionLabelJson2 = (ProjectLabelVersionJson) iterator.next();
				if (versionLabelJson2.label().equals(alias)){
					iterator.remove();
				}
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
		if (Constants.ALL_VERSION.equals(label)){
			return label;
		}
		Set<ProjectLabelVersionJson> versions = versions(projectName);
		for (ProjectLabelVersionJson versionLabelJson : versions) {
			if (versionLabelJson.label().equals(label)){
				return versionLabelJson.name();
			}
		}
		return label;
	}
}
