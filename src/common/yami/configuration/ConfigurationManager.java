package yami.configuration;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;

import org.apache.log4j.Logger;

import yami.model.Constants;

public class ConfigurationManager
{
	private static final Logger log = Logger.getLogger(ConfigurationManager.class);
	private static ConfigurationManager instance = new ConfigurationManager();
	private Yami configuration = null;
	private Date updateTime = null;
	
	private ConfigurationManager()
	{
		updateTime = new Date();
		try
		{
			configuration = getConfFromFile(Constants.getConfPath());
			applySystemPropertiesOverXML();
		}
		catch (RuntimeException e)
		{
			log.warn("Failed to read new configuration from " + Constants.getConfPath() + ". Using original from " + DateFormat.getDateTimeInstance().format(updateTime), e);
		}
		catch (Exception e)
		{
			log.warn("Configuration manager failed to start, aborting.", e);
		}
		
	}
	
	private void applySystemPropertiesOverXML()
	{
		/*
		 * if (System.getProperty("client.port") != null) { configuration.conf.clientport =
		 * Integer.parseInt(System.getProperty("client.port")); } if (System.getProperty("server.port") != null) {
		 * configuration.conf.serverport = Integer.parseInt(System.getProperty("server.port")); } if
		 * (System.getProperty("client.path") != null) { configuration.conf.clientpath =
		 * System.getProperty("client.path"); } if (System.getProperty("yami.conf") != null) {
		 * configuration.conf.conffile = System.getProperty("yami.conf"); }
		 */}
	
	public Yami getConfFromFile(String confPath) throws RuntimeException
	{
		try
		{
			Object o = JAXBContext.newInstance(Yami.class).createUnmarshaller().unmarshal(new File(confPath));
			return (Yami)o;
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public static ConfigurationManager getInstance()
	{
		return instance;
	}
	
	synchronized public Yami getCurrentConfiguration()
	{
		return configuration;
	}
	
	synchronized public void setCurrentConfiguration(Yami newConf)
	{
		configuration = newConf;
	}
	
	synchronized public Project getConfiguredProject()
	{
		if (null == configuration)
		{
			return null;
		}
		return configuration.project;
	}
	
	synchronized public GlobalConfiguration getCurrentGlobalConfiguration()
	{
		if (null == configuration)
		{
			return null;
		}
		return configuration.conf;
	}
}
