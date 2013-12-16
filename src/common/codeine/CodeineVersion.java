package codeine;


public class CodeineVersion
{
	public static final String major = VersionResourceReader.getString("CodeineVersion.major"); //$NON-NLS-1$
	public static final String minor = VersionResourceReader.getString("CodeineVersion.minor"); //$NON-NLS-1$
	public static final String build = VersionResourceReader.getString("CodeineVersion.build"); //$NON-NLS-1$
	public static final String date = VersionResourceReader.getString("CodeineVersion.date"); //$NON-NLS-1$
	
	public static String get()
	{
		return major + "." + minor + "." + build + "." + date; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static String getNoDate() {
		return major + "." + minor + "." + build; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
