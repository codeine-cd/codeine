package codeine.utils;

import java.lang.reflect.Type;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonFileUtils {
	
	private static final Logger log = Logger.getLogger(JsonFileUtils.class);

	private Gson gson;
	
	@Inject
	public JsonFileUtils(Gson gson) {
		super();
		this.gson = gson;
	}


	public <T> T getConfFromFile(String file, Class<T> clazz) {
		log.info("parsing file " + file);
		if (!FilesUtils.exists(file)) {
			return ReflectionUtils.newInstance(clazz);
		}
		return gson.fromJson(TextFileUtils.getContents(file), clazz);
	}
	public <T> T getConfFromFile(String file, Type clazz, T defaultValue) {
		log.info("parsing file " + file);
		if (!FilesUtils.exists(file)) {
			return defaultValue;
		}
		T $ = null;
		try {
			$ = gson.fromJson(TextFileUtils.getContents(file), clazz);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return $ == null ? defaultValue : $;
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
