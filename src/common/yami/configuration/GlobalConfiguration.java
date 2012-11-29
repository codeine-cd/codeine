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
		result = prime * result + ((clientpath == null) ? 0 : clientpath.hashCode());
		result = prime * result + clientrport;
		result = prime * result + ((perl == null) ? 0 : perl.hashCode());
		result = prime * result + ((rsync == null) ? 0 : rsync.hashCode());
		result = prime * result + serverport;
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
		if (clientpath == null)
		{
			if (other.clientpath != null)
				return false;
		}
		else if (!clientpath.equals(other.clientpath))
			return false;
		if (clientrport != other.clientrport)
			return false;
		if (perl == null)
		{
			if (other.perl != null)
				return false;
		}
		else if (!perl.equals(other.perl))
			return false;
		if (rsync == null)
		{
			if (other.rsync != null)
				return false;
		}
		else if (!rsync.equals(other.rsync))
			return false;
		if (serverport != other.serverport)
			return false;
		return true;
	}
	

	
}
