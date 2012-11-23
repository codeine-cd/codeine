package yami;


public class YamiVersion
{
	public static final String major = "0";
	public static final String minor = "1";
	public static final String build = "1";
	public static final String date = "20121124";
	
	public static String get()
	{
		return major + "." + minor + "." + build + "." + date;
	}
}