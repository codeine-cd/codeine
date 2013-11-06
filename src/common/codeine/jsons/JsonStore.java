package codeine.jsons;

import javax.inject.Inject;

import codeine.utils.JsonFileUtils;

import com.google.inject.Provider;


public class JsonStore<T> implements Provider<T>{

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
			storedJson = jsonFileUtils.getConfFromFile(path(), type());
		}
		return storedJson;
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
