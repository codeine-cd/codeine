package codeine.utils.os;

public class OsUtils {

	public static OperatingSystem getHostOs(){
		OperatingSystem os = getHostOsOrNull();
		if (null == os) {
			throw new IllegalStateException("now handle for os " + getOsProperty());
		}
		return os;
	}

	private static OperatingSystem getHostOsOrNull() {
		if (getOsProperty().startsWith("Windows")) {
			return OperatingSystem.Windows;
		}
		if (isLinux()) {
			return OperatingSystem.Linux;
		}
		return null;
	}

	public static boolean isLinux() {
		return getOsProperty().equals("Linux");
	}

	private static String getOsProperty() {
		return System.getProperty("os.name");
	}
}
