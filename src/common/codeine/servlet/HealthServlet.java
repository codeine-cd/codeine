package codeine.servlet;

import codeine.servlets.ReloadConfigurationServlet;
import com.codahale.metrics.health.HealthCheck.Result;
import com.codahale.metrics.health.HealthCheckRegistry;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class HealthServlet extends AbstractApiServlet {

    private static final Logger log = Logger.getLogger(HealthServlet.class);
    private final HealthCheckRegistry healthCheckRegistry;

    @Inject
    public HealthServlet(HealthCheckRegistry healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
    }

    @Override
    public void myGet(HttpServletRequest request, HttpServletResponse response) {
        SortedMap<String, Result> res = healthCheckRegistry.runHealthChecks();
        List<Entry<String, Result>> failedHealthChecks = res.entrySet().stream()
            .filter(stringResultEntry -> !stringResultEntry.getValue().isHealthy()).collect(Collectors.toList());
        if (!failedHealthChecks.isEmpty()) {
            failedHealthChecks.forEach(stringResultEntry -> {
                log.warn("Failed health check " + stringResultEntry.getKey());
                log.warn("Failed health check " + stringResultEntry.getValue().getMessage());
                Throwable error = stringResultEntry.getValue().getError();
                if (error != null) {
                    log.error("Error is", error);
                }
            });
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            log.info("Responded to health check: " + HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("Responded to health check: " + HttpServletResponse.SC_OK);
        }
        writeResponseJson(response, res);
    }

    @Override
    protected boolean checkPermissions(HttpServletRequest request) {
        return true;
    }

}