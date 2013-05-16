package yami.model;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.log4j.Logger;

import yami.configuration.GlobalConfiguration;
import yami.configuration.Project;
import yami.configuration.Yami;

public class DataStoreRetriever
{
	private static final Logger log = Logger.getLogger(DataStoreRetriever.class);
	private static DataStore dataStore = new DataStore();
	
	public static DataStore getD()
	{
		return dataStore;
	}
	
	public Project readProject()
	{
		try
		{
			Unmarshaller unmarshalr = createUnmarshaller();
			Object o = unmarshalr.unmarshal(new File(Constants.getConfPath()));
			return ((Yami)o).project;
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private static Unmarshaller createUnmarshaller() throws JAXBException
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
		return unmarshalr;
	}
	
	public static GlobalConfiguration readGlobalConfiguration()
	{
		try
		{
			Unmarshaller unmarshalr = createUnmarshaller();
			Object o = unmarshalr.unmarshal(new File(Constants.getConfPath()));
			return ((Yami)o).conf;
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException(ex);
		}
	}	
}
