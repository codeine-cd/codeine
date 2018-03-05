package codeine;

import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.info.CodeineRuntimeInfo;
import codeine.model.Constants;
import codeine.servlet.GeneralServletModule;
import codeine.stdout.StdoutRedirectToLog;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceFilter;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.filter.MetricsFilter;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.jetty.JettyStatisticsCollector;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import java.util.List;
import javax.servlet.DispatcherType;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;

public abstract class AbstractCodeineBootstrap {

    public AbstractCodeineBootstrap() {

    }

    public AbstractCodeineBootstrap(Injector injector) {
        this.injector = injector;
    }

    protected static void boot(String component, Class<? extends AbstractCodeineBootstrap> clazz) {
        new CodeineLogBootstrap().init(component, "codeine_" + component + ".log");
        if (!CodeineLogBootstrap.logToStdout()) {
            StdoutRedirectToLog.redirect();
        }
        Logger log = Logger.getLogger(AbstractCodeineBootstrap.class);
        Thread.setDefaultUncaughtExceptionHandler(new CodeineUncaughtExceptionHandler());
        try {
            clazz.newInstance().execute1(component);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error - exiting", e);
            System.exit(1);
        }
        log.info("boot sequence finished");
    }

    final Logger log = Logger.getLogger(AbstractCodeineBootstrap.class);

    private Injector injector;

    protected void execute1(String component) throws Exception {
        System.out.println("Starting codeine " + component + " at version " + CodeineVersion.get());

        injector = Guice.createInjector(getModules(component));
        FilterHolder guiceFilter = new FilterHolder(injector.getInstance(GuiceFilter.class));
        ServletContextHandler handler = createServletContextHandler();

        if (injector.getInstance(GlobalConfigurationJsonStore.class).get().prometheus_enabled()) {
            handler.addServlet(new ServletHolder(new MetricsServlet()), Constants.METRICS_CONTEXT);
        }

        handler.setContextPath("/");
        FilterHolder promHolder = new FilterHolder(
            new MetricsFilter("webapp_metrics_filter", "prometheus jetty metrics", 4, null));
        handler.addFilter(promHolder, "/api/*", EnumSet.allOf(DispatcherType.class));
        handler.addFilter(promHolder, "/api-with-token/*", EnumSet.allOf(DispatcherType.class));

        FilterHolder crossHolder = new FilterHolder(new CrossOriginFilter());
        crossHolder
            .setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, POST, PUT, DELETE");
        crossHolder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
            "X-Requested-With,Origin,Content-Type,Accept,api_token");

        handler.addFilter(crossHolder, "/api/*", EnumSet.allOf(DispatcherType.class));
        handler.addFilter(crossHolder, "/api-with-token/*", EnumSet.allOf(DispatcherType.class));
        handler.addFilter(guiceFilter, "/*", EnumSet.allOf(DispatcherType.class));
        createAdditionalServlets(handler);
        ContextHandlerCollection contexts = createFileServerContexts();
        contexts.addHandler(handler);
        int port = startServer(contexts);
        log.info("jetty started on port " + port);
        injector.getInstance(CodeineRuntimeInfo.class).setPort(port);
        execute();
    }

    private void registerPrometheus() {
        log.info("Add prometheus servlet and metrics");
        Server server = injector.getInstance(Server.class);
        DefaultExports.initialize();
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(server.getHandler());
        server.setHandler(stats);
        new JettyStatisticsCollector(stats).register();
    }

    protected int startServer(ContextHandlerCollection contexts) throws Exception {
        return startServer(contexts, injector.getInstance(Server.class));
    }

    protected int startServer(ContextHandlerCollection contexts, Server jettyServer)
        throws Exception {
        ((HttpConnectionFactory) jettyServer.getConnectors()[0].getConnectionFactories().iterator()
            .next()).getHttpConfiguration().setRequestHeaderSize(30000);
        jettyServer.setHandler(contexts);
        if (injector.getInstance(GlobalConfigurationJsonStore.class).get().prometheus_enabled()) {
            registerPrometheus();
        }
        jettyServer.start();
        return ((ServerConnector) jettyServer.getConnectors()[0]).getLocalPort();
    }

    protected void createAdditionalServlets(ServletContextHandler handler) {
        handler.addServlet(InvalidRequestServlet.class, "/*");
    }

    protected ServletContextHandler createServletContextHandler() {
        return new ServletContextHandler();
    }

    // returns a collection of contexts (Context per node)
    private ContextHandlerCollection createFileServerContexts() {
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        addHandler(Constants.RESOURCESS_CONTEXT, Constants.getResourcesDir(), contexts);
        contexts.addHandler(
            createStaticContextHandler("/favicon.ico", Constants.getAngularDir() + "/favicon.ico"));
        specificCreateFileServer(contexts);
        return contexts;
    }

    protected void specificCreateFileServer(ContextHandlerCollection contexts) {
    }

    protected void addHandler(String contextPath, String filesPath,
        ContextHandlerCollection contexts) {
        createFileSystem(filesPath);
        contexts.addHandler(createStaticContextHandler(contextPath, filesPath));
        log.info("context " + contextPath + " is serving " + filesPath);
    }

    // create the directory structure under "path" if does not already exists:
    private void createFileSystem(String path) {
        log.debug("Will try to create directory structure: " + path);
        File f = new File(path);
        if (f.exists()) {
            return;
        }
        if (!f.mkdirs()) {
            log.fatal("Failed to create directory structure: " + path);
            throw new RuntimeException("no write permission to create dir " + path);
        }
    }

    private int getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name.contains("@")) {
            name = name.substring(0, name.indexOf('@'));
        }
        return Integer.valueOf(name);
    }

    private ContextHandler createStaticContextHandler(String contextPath, String fsPath) {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{
            "index.htm", "index.html"
        });
        resourceHandler.setResourceBase(fsPath);
        ContextHandler ch = new ContextHandler();
        ch.setContextPath(contextPath);
        ch.setHandler(resourceHandler);
        return ch;
    }

    private Module[] getModules(final String component) {
        List<Module> $ = Lists.<Module>newArrayList(new GeneralServletModule(),
            new CodeineGeneralModule(), new BaseModule(component));
        $.addAll(getGuiceModules());
        return $.toArray(new Module[]{});
    }

    private final class BaseModule extends AbstractModule {

        private final String component;

        private BaseModule(String component) {
            this.component = component;
        }

        @Override
        protected void configure() {
            //		        binder().requireExplicitBindings();
            bind(CodeineRuntimeInfo.class)
                .toInstance(new CodeineRuntimeInfo(CodeineVersion.get(), component, getPid()));
            bind(GuiceFilter.class);
            bind(Server.class).toProvider(new ServerProvider()).in(Scopes.SINGLETON);
        }
    }

    public class ServerProvider implements Provider<Server> {

        @Override
        public Server get() {
            int port = getHttpPort();
            return new Server(port);
        }
    }

    protected abstract List<Module> getGuiceModules();

    public abstract int getHttpPort();

    protected abstract void execute() throws Exception;

    protected Injector injector() {
        return injector;
    }

}
