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
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.model.Constants;
import codeine.users.LoginServlet;
import codeine.users.LogoutServlet;
import codeine.users.UsersManager;

import com.google.common.collect.Lists;
import com.google.inject.Module;

public class CodeineWebServerBootstrap extends AbstractCodeineBootstrap
{
	public static void main(String[] args)
	{
		boot(Component.WEB, CodeineWebServerBootstrap.class);
	}
	
	@Override
	protected void execute() {
		new Thread(new PeriodicExecuter(UpdaterThreadV3.SLEEP_TIME ,injector().getInstance(UpdaterThreadV3.class), "UpdaterThreadV3")).start();
	}

	@Override
	protected List<Module> getGuiceModules() {
		return Lists.<Module>newArrayList(new ServerModule(), new ServerServletModule());
	}

	@Override
	public int getHttpPort() {
		return injector().getInstance(GlobalConfigurationJson.class).web_server_port();
	}
	@Override
	protected ServletContextHandler createServletContextHandler() {
		AuthenticationMethod a = injector().getInstance(GlobalConfigurationJson.class).authentication_method();
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

		context.addServlet(new ServletHolder(injector().getInstance(LoginServlet.class)), "/login");
		context.addServlet(new ServletHolder(injector().getInstance(LogoutServlet.class)), "/logout");

//		Constraint constraint = new Constraint();
//		constraint.setName(Constraint.__FORM_AUTH);
//		constraint.setRoles(new String[] { "user", "admin", "moderator" });
//		constraint.setAuthenticate(true);
//
//		ConstraintMapping constraintMapping = new ConstraintMapping();
//		constraintMapping.setConstraint(constraint);
//		constraintMapping.setPathSpec("/*");

		ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
//		securityHandler.addConstraintMapping(constraintMapping);
		UsersManager usersManager = injector().getInstance(UsersManager.class);
		usersManager.initUsers();
		securityHandler.setLoginService(usersManager.loginService());

		FormAuthenticator authenticator = new FormAuthenticator("/login", "/login", false);
		securityHandler.setAuthenticator(authenticator);

		context.setSecurityHandler(securityHandler);
		return context;
	}
	private ServletContextHandler createServletContextHandlerSpnego() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS
				| ServletContextHandler.SECURITY);

		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__SPNEGO_AUTH);
		constraint.setRoles(new String[] {"GER.CORP.INTEL.COM","GAR.CORP.INTEL.COM","AMR.CORP.INTEL.COM","CCR.CORP.INTEL.COM"});
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
