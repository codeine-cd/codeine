package codeine.version;

import java.util.Map;

import codeine.jsons.info.VersionInfo;

import com.google.common.collect.Maps;

public class VersionsMapping {
	
	private Map<String, VersionInfo> versions = Maps.newHashMap();

	public void update(VersionInfo versionInfo) {
		versions.put(versionInfo.alias, versionInfo);
	}

	protected VersionInfo info(String version) {
		return versions.get(version);
	}
	
}
