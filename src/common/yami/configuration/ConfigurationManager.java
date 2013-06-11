package yami.configuration;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.log4j.Logger;

import yami.model.Constants;

public class ConfigurationManager
{
	private static final Logger log = Logger.getLogger(ConfigurationManager.class);
	
	private Yami configuration = null;
	
	public ConfigurationManager()
	{
		try
		{
			configuration = getConfFromFile(Constants.getConfPath());
			applySystemPropertiesOverXML();
		}
		catch (Exception e)
		{
			log.error("Failed to read new configuration from " + Constants.getConfPath(), e);
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
		 */
	}
	
	public Yami getConfFromFile(String confPath) throws RuntimeException
	{
		try
		{
			Unmarshaller unmarshalr = JAXBContext.newInstance(Yami.class).createUnmarshaller();
			unmarshalr.setEventHandler(new ValidationEventHandler()
			{
				@Override
				public boolean handleEvent(ValidationEvent event)
				{
					log.warn("error in xml: " + event, new RuntimeException());
					return true;
				}
			});
			Object o = unmarshalr.unmarshal(new File(confPath));
			return (Yami)o;
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public Yami getCurrentConfiguration()
	{
		return configuration;
	}
	
	public Project getConfiguredProject()
	{
		return configuration.project;
	}
	
	public GlobalConfiguration getCurrentGlobalConfiguration()
	{
		return configuration.conf;
	}

	public Project getD() {
		return getConfiguredProject();
	}
}
