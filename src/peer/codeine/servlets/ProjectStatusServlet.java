package codeine.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.jsons.peer_status.PeerStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

public class ProjectStatusServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(ProjectStatusServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject private PeerStatus peerStatus;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.info("ProjectStatusServlet started");
		res.getWriter().print(gson.toJson(peerStatus));
	}
}
