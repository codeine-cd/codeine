package codeine.version;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.utils.TextFileUtils;

import com.google.gson.Gson;

public class PersistentFileJsonProvider {

	private static final Logger log = Logger.getLogger(PersistentFileJsonProvider.class);
	
	@Inject
	private Gson gson;

	public PersistentFileJsonProvider() {
		super();
	}

	public <T> T get(String file2, Class<T> classOfT, T defaultValue) {
		File file = new File(file2); 
		if (!file.exists())
		{
			new File(file.getParent()).mkdirs();
			try {
				file.createNewFile();
				TextFileUtils.setContents(file2, gson.toJson(defaultValue));
			} catch (IOException e) {
				log.warn("failed to create version mapping file " + file2, e);
				throw new RuntimeException(e);
			}
			return defaultValue;
		}
		String contents = TextFileUtils.getContents(file2);
		return gson.fromJson(contents, classOfT);
	}

}