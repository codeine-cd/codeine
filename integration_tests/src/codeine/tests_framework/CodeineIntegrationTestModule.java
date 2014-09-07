package codeine.tests_framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;

public class CodeineIntegrationTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Gson.class).toInstance(new GsonBuilder().setPrettyPrinting().create());
	}


}
