package yami.configuration;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.xml.bind.*;

import org.apache.log4j.*;

import yami.model.*;

public class ConfigurationManager
{
	private static final Logger log = Logger.getLogger(ConfigurationManager.class);
	private static ConfigurationManager instance = new ConfigurationManager();
	private Yami configuration = null;
	private Date updateTime = null;
	
	private ConfigurationManager()
	{
		try
		{
			configuration = getConfFromFile(Constants.getConfPath());
			applySystemPropertiesOverXML();
			updateTime = new Date();
		}
		catch (RuntimeException e)
		{
			log.warn("Failed to read new configuration from " + Constants.getConfPath() + ". Using original from " + DateFormat.getDateTimeInstance().format(updateTime), e);
		}
	}
	
	private void applySystemPropertiesOverXML()
	{
		if (System.getProperty("client.port") != null)
		{
			configuration.conf.clientport = Integer.parseInt(System.getProperty("client.port"));
		}
		if (System.getProperty("server.port") != null)
		{
			configuration.conf.serverport = Integer.parseInt(System.getProperty("server.port"));
		}
		if (System.getProperty("client.path") != null)
		{
			configuration.conf.clientpath = System.getProperty("client.path");
		}
		if (System.getProperty("yami.conf") != null)
		{
			configuration.conf.confPath = System.getProperty("yami.conf");
		}
	}
	
	public Yami getConfFromFile(String confPath) throws RuntimeException
	{
		try
		{
			Object o = JAXBContext.newInstance(Yami.class).createUnmarshaller().unmarshal(new File(Constants.getConfPath()));
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
