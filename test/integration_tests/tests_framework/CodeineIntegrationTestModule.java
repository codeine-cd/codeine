package integration_tests.tests_framework;


public class CodeineIntegrationTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Gson.class).toInstance(new GsonBuilder().setPrettyPrinting().create());
	}


}
