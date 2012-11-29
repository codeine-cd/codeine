package yami.configuration;

public class GlobalConfiguration
{
	public String rsync = "rsync";
	public String perl = "perl";
	public String clientpath = "/tmp/yami.monitor/";
	public int serverport = 8080;
	public int clientrport = 8112;
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = result * prime  + ((rsync == null) ? 0 : rsync.hashCode());
		result = result * prime  + ((perl == null) ? 0 : perl.hashCode());
		result = result * prime  + 8080;
		result = result * prime  + 8112;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GlobalConfiguration other = (GlobalConfiguration)obj;
		if (rsync == null)
		{
			if (other.rsync != null)
				return false;
		}
		else if (!rsync.equals(other.rsync))
			return false;
		return true;
	}
}
