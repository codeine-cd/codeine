package codeine.servlets.api_servlets.angular;

import codeine.configuration.PathHelper;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;
import com.google.common.io.ByteStreams;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class CommandOutputApiServlet extends AbstractApiServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(CommandStatusApiServlet.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Inject
    private PathHelper pathHelper;

    @Override
    protected boolean checkPermissions(HttpServletRequest request) {
        return canReadProject(request);
    }

    @Override
    protected void myGet(final HttpServletRequest request, final HttpServletResponse response) {
        final String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
        final String commandID = getParameter(request, Constants.UrlParameters.COMMAND_ID);
        log.info("Will return command " + commandID + " of project " + projectName + " output");
        request.startAsync();
        final String outfile = pathHelper.getCommandOutputFile(projectName, commandID);
        FutureTask futureProcess = new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    ByteStreams.copy(new FileInputStream(outfile), request.getAsyncContext().getResponse().getOutputStream());
                } catch (IOException e) {
                    log.error("Failed to copy stream", e);
                    writeResponseJson(response, e);
                    return false;
                }finally {
                    log.debug("Finished async request for command " + commandID + " of project " + projectName);
                    request.getAsyncContext().complete();
                }
                return true;
            }
        });
        executorService.execute(futureProcess);
        futureProcess.run();
    }
}
