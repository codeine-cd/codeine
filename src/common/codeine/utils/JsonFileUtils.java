package codeine.utils;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class JsonFileUtils {
	
	private static final Logger log = Logger.getLogger(JsonFileUtils.class);

	private Gson gson;
	
	@Inject
	public JsonFileUtils(Gson gson) {
		super();
		this.gson = gson;
	}


	public <T> T getConfFromFile(String file, Class<T> clazz)
	{
		log.info("parsing file " + file);
		return gson.fromJson(TextFileUtils.getContents(file), clazz);
	}


	public <T> void setContent(String file, T json) {
		TextFileUtils.setContents(file, gson.toJson(json));
	}


	@SuppressWarnings("unchecked")
	public <T> T getConfFromFile(String file, T defaultValue) {
		T $ = null;
		try {
			$ = (T) getConfFromFile(file, defaultValue.getClass());
		} catch (Exception e) {
			log.info("could not get conf from file, will use default " + e.getMessage());
			log.debug("e", e);
		}
		return $ == null ? defaultValue : $;
	}
}
