package yami.model;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import yami.configuration.Project;

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
			Unmarshaller unmarshalr;
			unmarshalr = createUnmarshaller(Project.class);
			Object $ = unmarshalr.unmarshal(new File(Constants.getConf()));
			return (Project)$;
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	private <T> Unmarshaller createUnmarshaller(Class<T> clazz) throws JAXBException
	{
		Unmarshaller unmarshalr;
		unmarshalr = JAXBContext.newInstance(clazz).createUnmarshaller();
		unmarshalr.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
//		unmarshalr.setEventHandler(new ValidationEventHandler()
//		{
//			@Override
//			public boolean handleEvent(ValidationEvent event)
//			{
//				collector.addViolation(new CoValidationViolationEvent(event));
//				return true;
//			}
//		});
//		unmarshalr.setListener(new Listener()
//		{
//			@Override
//			public void afterUnmarshal(Object target, Object parent)
//			{
//				if (target instanceof AbstractCo)
//				{
//					((AbstractCo)target).fixUknowns();
//					if (parent instanceof AbstractCo || parent == null)
//					{
//						((AbstractCo)target).setParent((AbstractCo)parent);
//						((AbstractCo)target).afterUnmarshal((AbstractCo)parent, collector);
//					}
//				}
//			}
//		});
		return unmarshalr;
	}
	
	
}
