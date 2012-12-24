package yami.configuration;


public class GlobalConfiguration
{
	private String rsyncuser = "root";
	private String java = "java";
	private String rsyncsource = "";
	private String rsync = "rsync";
	private String perl = "perl";
	private String conffile = "yami.conf.xml";
	private String clientpath = "/tmp/yami.monitor/";
	private int serverport = 8080;
	private int clientport = 8112;
	
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
			rsync = System.getProperty("rsync.path");
		}
		return rsync;
	}
	
	public String getRsyncUser()
	{
		if (System.getProperty("rsync.user") != null)
		{
			rsyncuser = System.getProperty("rsync.user");
		}
		return rsyncuser;
	}
	
	public int getClientPort()
	{
		if (System.getProperty("client.port") != null)
		{
			clientport  = Integer.parseInt(System.getProperty("client.port"));
		}
		return clientport;
	}
	
	public int getServerPort()
	{
		if (System.getProperty("server.port") != null)
		{
			serverport = Integer.parseInt(System.getProperty("server.port"));
		}
		return serverport;
	}
	
	public String getRsyncSource()
	{
		if (System.getProperty("rsync.source") != null)
		{
			rsyncsource = System.getProperty("rsync.source");
		}
		return rsyncsource;
	}
	
	@Override
	public String toString()
	{
		return "GlobalConfiguration [rsyncuser=" + rsyncuser + ", java=" + java + ", rsyncsource=" + rsyncsource + ", rsync=" + rsync + ", perl=" + perl + ", conffile=" + conffile + ", clientpath=" + clientpath + ", serverport=" + serverport + ", clientport=" + clientport + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientpath == null) ? 0 : clientpath.hashCode());
		result = prime * result + clientport;
		result = prime * result + ((conffile == null) ? 0 : conffile.hashCode());
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
		if (conffile == null)
		{
			if (other.conffile != null)
				return false;
		}
		else if (!conffile.equals(other.conffile))
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
	
}
