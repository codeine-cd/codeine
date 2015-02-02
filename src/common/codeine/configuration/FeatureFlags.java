package codeine.configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import codeine.model.Constants;

public class FeatureFlags {

	public boolean isCollectorsDisabled() {
		Path path = Paths.get(Constants.getFeatureFlagsDir() + File.pathSeparator + "collectors_disabled");
		return Files.exists(path);
	}
}
