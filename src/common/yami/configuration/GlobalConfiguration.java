package yami.configuration;

public class GlobalConfiguration
{
	public String rsyncuser = "root";
	public String java = "java";
	public String rsyncsource = "";
	public String rsync = "rsync";
	public String perl = "perl";
	public String confPath = null;
	public String clientpath = "/tmp/yami.monitor/";
	public int serverport = 8080;
	public int clientport = 8112;
	
	public String toString()
	{
		return "rsyncuser = " + rsyncuser + ", " + "rsyncsource = " + rsyncsource  +", "+ "rsync = " + rsync + ", "+ "perl = " + perl + ", "+ "clientpath = " + clientpath+ ", "+ "clientport = " + clientport + ", "+ "serverport = " + serverport;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientpath == null) ? 0 : clientpath.hashCode());
		result = prime * result + clientport;
		result = prime * result + ((confPath == null) ? 0 : confPath.hashCode());
		result = prime * result + ((java == null) ? 0 : java.hashCode());
		result = prime * result + ((perl == null) ? 0 : perl.hashCode());
		result = prime * result + ((rsync == null) ? 0 : rsync.hashCode());
		result = prime * result + ((rsyncsource == null) ? 0 : rsyncsource.hashCode());
		result = prime * result + ((rsyncuser == null) ? 0 : rsyncuser.hashCode());
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
		if (clientport != other.clientport)
			return false;
		if (confPath == null)
		{
			if (other.confPath != null)
				return false;
		}
		else if (!confPath.equals(other.confPath))
			return false;
		if (java == null)
		{
			if (other.java != null)
				return false;
		}
		else if (!java.equals(other.java))
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
		if (rsyncsource == null)
		{
			if (other.rsyncsource != null)
				return false;
		}
		else if (!rsyncsource.equals(other.rsyncsource))
			return false;
		if (rsyncuser == null)
		{
			if (other.rsyncuser != null)
				return false;
		}
		else if (!rsyncuser.equals(other.rsyncuser))
			return false;
		if (serverport != other.serverport)
			return false;
		return true;
	}


	public String getJavaPath()
	{
		if (System.getProperty("java.path") != null)
		{
			java = System.getProperty("java.path");
		}
		return java;
	}

	public String getRsyncPath()
	{
		if (System.getProperty("rsync.path") != null)
		{
			java = System.getProperty("rsync");
		}
		return rsync;
	}
}
