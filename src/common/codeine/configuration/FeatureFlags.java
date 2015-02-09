package codeine.configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import codeine.model.Constants;

public class FeatureFlags {

	public boolean isCollectorsDisabled() {
		return isFeatureExists("collectors_disabled");
	}

	private boolean isFeatureExists(String file) {
		Path path = Paths.get(Constants.getFeatureFlagsDir() + File.pathSeparator + file);
		return Files.exists(path);
	}

	public boolean isNotificationsDisabled() {
		return isFeatureExists("notifications_disabled");
	}
}
