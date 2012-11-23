package yami;


public class YamiVersion
{
	public static final String major = VersionResourceReader.getString("YamiVersion.major"); //$NON-NLS-1$
	public static final String minor = VersionResourceReader.getString("YamiVersion.minor"); //$NON-NLS-1$
	public static final String build = VersionResourceReader.getString("YamiVersion.build"); //$NON-NLS-1$
	public static final String date = VersionResourceReader.getString("YamiVersion.date"); //$NON-NLS-1$
	
	public static String get()
	{
		return major + "." + minor + "." + build + "." + date; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
