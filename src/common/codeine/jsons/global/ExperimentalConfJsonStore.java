package codeine.jsons.global;

import codeine.jsons.JsonStore;
import codeine.model.Constants;

public class ExperimentalConfJsonStore extends JsonStore<ExperimentalConfJson>{

	public ExperimentalConfJsonStore() {
		super(Constants.getExperimentalConfPath(), ExperimentalConfJson.class);
	}

}
