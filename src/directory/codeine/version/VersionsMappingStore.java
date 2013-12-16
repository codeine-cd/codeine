package codeine.version;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.PathHelper;
import codeine.utils.TextFileUtils;

import com.google.gson.Gson;

public class VersionsMappingStore {

	private static final Logger log = Logger.getLogger(VersionsMappingStore.class);
	
	private @Inject VersionsMapping versionsMapping;
	private @Inject PathHelper pathHelper;
	private @Inject Gson gson;
	
	public void store() {
		log.info("store version map");
		TextFileUtils.setContents(pathHelper.getVersionMappingFile(), gson.toJson(versionsMapping));
	}
}
