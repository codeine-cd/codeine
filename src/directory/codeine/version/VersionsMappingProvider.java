package codeine.version;

import javax.inject.Inject;
import javax.inject.Provider;

import codeine.configuration.PathHelper;

public class VersionsMappingProvider extends PersistentFileJsonProvider implements Provider<VersionsMapping>{

	@Inject
	private PathHelper pathHelper;
	
	@Override
	public VersionsMapping get() {
		return get(pathHelper.getVersionMappingFile(), VersionsMapping.class, new VersionsMapping());
	}

}
