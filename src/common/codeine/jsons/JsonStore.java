package codeine.jsons;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.utils.JsonFileUtils;

import com.google.inject.Provider;


public class JsonStore<T> implements Provider<T>{

	private static final Logger log = Logger.getLogger(JsonStore.class);
	private @Inject JsonFileUtils jsonFileUtils;
	private String path;
	private Class<T> type;
	private T storedJson;

	public JsonStore(String path, Class<T> type) {
		super();
		this.path = path;
		this.type = type;
	}
	
	@Override
	public T get() {
		if (storedJson == null){
			log.info("loading configuration from " + path() + " for type " + type());
			storedJson = getNew();
		}
		return storedJson;
	}

	public T getNew() {
		return jsonFileUtils.getConfFromFile(path(), type());
	}
	
	public void store(T json){
		jsonFileUtils.setContent(path, json);
		storedJson = json;
	}
	
	private Class<T> type() {
		return type;
	}
	private String path() {
		return path;
	}

}
