package codeine;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.SpnegoLoginService;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.security.authentication.SpnegoAuthenticator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;

import codeine.command_peer.CommandFileWriter;
import codeine.configuration.PathHelper;
import codeine.executer.PeriodicExecuter;
import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.jsons.peer_status.PeersProjectsStatusInWebServer;
import codeine.mail.NotificationsFetchAndUpdateTask;
import codeine.model.Constants;
import codeine.servlet.UsersManager;
import codeine.servlets.AngularServlet;
import codeine.statistics.IMonitorStatistics;
import codeine.statistics.MonitorsStatistics;
import codeine.users.LogoutServlet;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;

public class CodeineWebServerBootstrap extends AbstractCodeineBootstrap
{
	public CodeineWebServerBootstrap(Injector injector) {
		super(injector);
	}

	public CodeineWebServerBootstrap() {
	}

	public static void main(String[] args)
	{
		System.setProperty("org.eclipse.jetty.server.Request.maxFormContentSize", "10000000");
		boot(Component.WEB, CodeineWebServerBootstrap.class);
	}
	
	@Override
	protected void execute() {
		log.info("executing web server bootstrap");
		new PeriodicExecuter(PeersProjectsStatusInWebServer.SLEEP_TIME ,injector().getInstance(PeersProjectsStatus.class)).runInThread();
		try {
			log.info("updating projects in db");
			injector().getInstance(ConfigurationManagerServer.class).updateDb();
		} catch (Exception e) {
			log.error("fail to update projects in db", e);
		}
		new PeriodicExecuter(MonitorsStatistics.SLEEP_TIME ,injector().getInstance(IMonitorStatistics.class)).runInThreadSleepFirst();
		new PeriodicExecuter(TimeUnit.SECONDS.toMillis(5), injector().getInstance(NotificationsFetchAndUpdateTask.class)).runInThreadSleepFirst();
		new PeriodicExecuter(TimeUnit.SECONDS.toMillis(5), injector().getInstance(CommandFileWriter.class)).runInThreadSleepFirst();
	}

	@Override
	protected void createAdditionalServlets(ServletContextHandler handler) {
		handler.addServlet(AngularServlet.class, "/*");
	}
	
	@Override
	protected List<Module> getGuiceModules() {
		return Lists.<Module>newArrayList(new WebServerModule(), new ServerServletModule());
	}

	@Override
	public int getHttpPort() {
		return injector().getInstance(GlobalConfigurationJsonStore.class).get().web_server_port();
	}
	@Override
	protected ServletContextHandler createServletContextHandler() {
		AuthenticationMethod a = injector().getInstance(GlobalConfigurationJsonStore.class).get().authentication_method();
		if (Boolean.getBoolean("ignoreSecurity")){
			a = AuthenticationMethod.Disabled;
		}
		switch (a){
		case Disabled:
			return super.createServletContextHandler();
		case WindowsCredentials:
			return createServletContextHandlerSpnego();
		case Builtin:
			return createServletContextHandlerBasic();
		}
		throw new IllegalArgumentException("auth method not found " + a);
	}
	
	@Override
	protected void specificCreateFileServer(ContextHandlerCollection contexts){
		PathHelper pathHelper = injector().getInstance(PathHelper.class);
		addHandler(Constants.PROJECT_FILES_CONTEXT, pathHelper.getProjectsDir() , contexts);
		addHandler(Constants.ANGULAR_RESOURCES_CONTEXT_PATH, Constants.getAngularDir(), contexts);
		addHandler("/components", Constants.getAngularDir() + "/components", contexts);
		addHandler("/styles", Constants.getAngularDir() + "/styles", contexts);
		addHandler("/scripts", Constants.getAngularDir() + "/scripts", contexts);
		addHandler("/fonts", Constants.getAngularDir() + "/fonts", contexts);
		addHandler("/bower_components", Constants.getAngularDir() + "/bower_components", contexts);
		addHandler("/images", Constants.getAngularDir() + "/images", contexts);
		addHandler("/views", Constants.getAngularDir() + "/views", contexts);
		addHandler("/lib", Constants.getAngularDir() + "/lib", contexts);
	}
	
	private ServletContextHandler createServletContextHandlerBasic(){
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS
				| ServletContextHandler.SECURITY);

		context.addServlet(new ServletHolder(injector().getInstance(LogoutServlet.class)), "/logout");

		ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
		UsersManager usersManager = injector().getInstance(UsersManager.class);
		usersManager.initUsers();
		securityHandler.setLoginService(usersManager.loginService());

		FormAuthenticator authenticator = new FormAuthenticator();//"/login", "/login", false);
		securityHandler.setAuthenticator(authenticator);

		context.setSecurityHandler(securityHandler);
		return context;
	}
	private ServletContextHandler createServletContextHandlerSpnego() {
		GlobalConfigurationJsonStore config = injector().getInstance(GlobalConfigurationJsonStore.class);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS
				| ServletContextHandler.SECURITY);

		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__SPNEGO_AUTH);
		constraint.setRoles(config.get().roles());
		constraint.setAuthenticate(true);

		ConstraintMapping constraintMapping = new ConstraintMapping();
		constraintMapping.setConstraint(constraint);
		constraintMapping.setPathSpec("/*");

		ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler(){
			@Override
			public void handle(String pathInContext, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
		    {
				response.addHeader("Access-Control-Allow-Origin", "*");
				if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
					response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE");
					response.addHeader("Access-Control-Allow-Headers",
					"X-Requested-With,Origin,Content-Type, Accept");
				}
				super.handle(pathInContext, baseRequest, request, response);
		    }
		};
		
		Constraint constraint2 = new Constraint();
		constraint2.setAuthenticate(false);
		constraint2.setName("reporter");
		constraint2.setRoles(config.get().roles());
		ConstraintMapping constraintMapping2 = new ConstraintMapping();
		constraintMapping2.setConstraint(constraint2);
		constraintMapping2.setPathSpec(Constants.apiContext(Constants.REPORTER_CONTEXT));

		Constraint constraint3 = new Constraint();
		constraint3.setAuthenticate(false);
		constraint3.setName("api_with_token");
		constraint3.setRoles(config.get().roles());
		ConstraintMapping constraintMapping3 = new ConstraintMapping();
		constraintMapping3.setConstraint(constraint3);
		constraintMapping3.setPathSpec(Constants.apiTokenContext("/*"));

		Constraint constraint4 = new Constraint();
		constraint4.setAuthenticate(false);
		constraint4.setName("prometheus");
		constraint4.setRoles(config.get().roles());
		ConstraintMapping constraintMapping4 = new ConstraintMapping();
		constraintMapping4.setConstraint(constraint4);
		constraintMapping4.setPathSpec(Constants.apiContext(Constants.METRICS_CONTEXT));

		securityHandler.addConstraintMapping(constraintMapping3);
		securityHandler.addConstraintMapping(constraintMapping2);
		securityHandler.addConstraintMapping(constraintMapping);
		SpnegoLoginService loginService = new SpnegoLoginService(null, Constants.getSpnegoPropertiesPath());
		securityHandler.setLoginService(loginService);
		
		final DefaultIdentityService idService = new DefaultIdentityService();
		loginService.setIdentityService(idService);
		SpnegoAuthenticator authenticator = new SpnegoAuthenticator();
		securityHandler.setAuthenticator(authenticator);

		context.setSecurityHandler(securityHandler);
		return context;
	}
}
