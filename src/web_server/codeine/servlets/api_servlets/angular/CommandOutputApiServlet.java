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

public class CommandOutputApiServlet extends AbstractApiServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(CommandStatusApiServlet.class);
    @Inject
    private PathHelper pathHelper;

    @Override
    protected boolean checkPermissions(HttpServletRequest request) {
        return canReadProject(request);
    }

    @Override
    protected void myGet(HttpServletRequest request, HttpServletResponse response) {
        String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
        String commandID = getParameter(request, Constants.UrlParameters.COMMAND_ID);
        log.info("Will return command " + commandID + " of project " + projectName + " output");
        request.startAsync();
        String outfile = pathHelper.getCommandOutputFile(projectName, commandID);
        try {
            ByteStreams.copy(new FileInputStream(outfile), request.getAsyncContext().getResponse().getOutputStream());
        } catch (IOException e) {
            log.error("Failed to copy stream", e);
            writeResponseJson(response, e);
        }
    }
}
