package codeine.jsons.global;

import codeine.jsons.JsonStore;
import codeine.model.Constants;

public class GlobalConfigurationJsonStore extends JsonStore<GlobalConfigurationJson>{

	public GlobalConfigurationJsonStore() {
		super(Constants.getConfPath(), GlobalConfigurationJson.class);
	}

}
