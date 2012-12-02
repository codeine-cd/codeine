package yami.model;

import java.io.*;

import javax.xml.bind.*;

import yami.configuration.*;

public class DataStoreRetriever
{
	private static DataStore dataStore = new DataStore();
	
	public static DataStore getD()
	{
		return dataStore;
	}
	
	public Project readProject()
	{
		try
		{
			Object o = JAXBContext.newInstance(Yami.class).createUnmarshaller().unmarshal(new File(Constants.getConf()));
			return ((Yami)o).project;
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public static GlobalConfiguration readGlobalConfiguration()
	{
		try
		{
			Object o = JAXBContext.newInstance(Yami.class).createUnmarshaller().unmarshal(new File(Constants.getConf()));
			return ((Yami)o).conf;
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException(ex);
		}
	}	
}
