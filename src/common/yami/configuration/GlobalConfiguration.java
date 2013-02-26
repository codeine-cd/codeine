package yami.configuration;

public class GlobalConfiguration
{
	
	public String rsync_user = "root";
	public String java = "java";
	public String rsync_source = "admin:/yami/";
	public String rsync = "rsync";
	public String perl = "perl";
	public String conf_file = "yami.conf.xml";
	public String client_path = "/tmp/yami.monitor/";
	public int server_port = 8080;
	public int client_port = 8112;
	
	public String getJavaPath()
	{
		if (System.getProperty("java") != null)
		{
			java = System.getProperty("java");
		}
		return java;
	}
	
	public String getRsyncPath()
	{
		if (System.getProperty("rsync") != null)
		{
			return System.getProperty("rsync");
		}
		return rsync;
	}
	
	public String getRsyncUser()
	{
		if (System.getProperty("rsync_user") != null)
		{
			rsync_user = System.getProperty("rsync_user");
		}
		return rsync_user;
	}
	
	public int getClientPort()
	{
		if (System.getProperty("client_port") != null)
		{
			client_port = Integer.parseInt(System.getProperty("client_port"));
		}
		return client_port;
	}
	
	public int getServerPort()
	{
		if (System.getProperty("server_port") != null)
		{
			server_port = Integer.parseInt(System.getProperty("server_port"));
		}
		return server_port;
	}
	
	public String getRsyncSource()
	{
		if (System.getProperty("rsync_source") != null)
		{
			rsync_source = System.getProperty("rsync_source");
		}
		return rsync_source;
	}
	
	public String getConfFileName()
	{
		if (System.getProperty("conf_file") != null)
		{
			conf_file = System.getProperty("conf_file");
		}
		return conf_file;
	}
	
	@Override
	public String toString()
	{
		return "GlobalConfiguration [rsyncuser=" + rsync_user + ", java=" + java + ", rsyncsource=" + rsync_source + ", rsync=" + rsync + ", perl=" + perl + ", conffile=" + conf_file + ", clientpath=" + client_path + ", serverport=" + server_port + ", clientport=" + client_port + "]";
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client_path == null) ? 0 : client_path.hashCode());
		result = prime * result + client_port;
		result = prime * result + ((conf_file == null) ? 0 : conf_file.hashCode());
		result = prime * result + ((java == null) ? 0 : java.hashCode());
		result = prime * result + ((perl == null) ? 0 : perl.hashCode());
		result = prime * result + ((rsync == null) ? 0 : rsync.hashCode());
		result = prime * result + ((rsync_source == null) ? 0 : rsync_source.hashCode());
		result = prime * result + ((rsync_user == null) ? 0 : rsync_user.hashCode());
		result = prime * result + server_port;
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
		if (client_path == null)
		{
			if (other.client_path != null)
				return false;
		}
		else if (!client_path.equals(other.client_path))
			return false;
		if (client_port != other.client_port)
			return false;
		if (conf_file == null)
		{
			if (other.conf_file != null)
				return false;
		}
		else if (!conf_file.equals(other.conf_file))
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
		if (rsync_source == null)
		{
			if (other.rsync_source != null)
				return false;
		}
		else if (!rsync_source.equals(other.rsync_source))
			return false;
		if (rsync_user == null)
		{
			if (other.rsync_user != null)
				return false;
		}
		else if (!rsync_user.equals(other.rsync_user))
			return false;
		if (server_port != other.server_port)
			return false;
		return true;
	}
	
}
