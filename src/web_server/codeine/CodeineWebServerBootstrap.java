package codeine;

import java.util.List;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.SpnegoLoginService;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.security.authentication.SpnegoAuthenticator;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;

import codeine.configuration.PathHelper;
import codeine.executer.PeriodicExecuter;
import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.model.Constants;
import codeine.servlet.UsersManager;
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
		new PeriodicExecuter(PeersProjectsStatus.SLEEP_TIME ,injector().getInstance(PeersProjectsStatus.class)).runInThread();
		try {
			injector().getInstance(ConfigurationManagerServer.class).updateDb();
		} catch (Exception e) {
			log.error("fail to update projects in db", e);
		}
		new PeriodicExecuter(Integer.MAX_VALUE ,injector().getInstance(ProjectConfigurationInPeerUpdater.class)).runInThread();
		new PeriodicExecuter(MonitorsStatistics.SLEEP_TIME ,injector().getInstance(MonitorsStatistics.class)).runInThread();
	}

	@Override
	protected List<Module> getGuiceModules() {
		return Lists.<Module>newArrayList(new ServerModule(), new ServerServletModule());
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

		ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
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
