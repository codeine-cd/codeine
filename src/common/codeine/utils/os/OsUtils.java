package codeine.utils.os;

public class OsUtils {

	public static OperatingSystem getHostOs(){
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			return OperatingSystem.Windows;
		}
		if (osName.equals("Linux")) {
			return OperatingSystem.Linux;
		}
		throw new IllegalStateException("now handle for os " + osName);
	}
}
