package yami.servlets;

import java.io.PrintWriter;

import yami.YamiVersion;
import yami.configuration.ConfigurationManager;
import yami.configuration.GlobalConfiguration;
import yami.model.Constants;

public class HtmlWriter
{
	public static void writeHeader(ConfigurationManager cm, GlobalConfiguration gc, String hostname, PrintWriter writer)
	{
		writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		writer.println("<head>");
		writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		// writer.println("<meta http-equiv=\"refresh\" content=\"5\" />");
		writer.println("<title>yami dashboard - " + cm.getConfiguredProject().name + "</title>");
		writer.println("<link rel=\"stylesheet\" href=\"../style.css\" type=\"text/css\" />");
		writer.println("<script src=\"../dashboard.js\" type=\"text/javascript\" ></script>");
		writer.println("");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<div id=\"container\">");
		writer.println("  <div id=\"header\">");
		writer.println("      <h1><a href=\"/\">yami</a></h1>");
		writer.println("        <h2>" + YamiVersion.get() + "</h2>");
		writer.println("        <div class=\"clear\"></div>");
		writer.println("    </div>");
		writer.println("    <div id=\"nav\">");
		writer.println("      <ul>");
		writer.println("          <li class=\"start\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.DASHBOARD_CONTEXT
				+ "\">Dashboard</a></li>");
		writer.println("          <li class=\"start\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.DASHBOARD_CONTEXT + "?alerts=true"
				+ "\">Alerts</a></li>");
		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.AGGREGATE_NODE_CONTEXT
				+ "\">Aggregate</a></li>");
		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.PEERS_DASHBOARD_CONTEXT
				+ "\">Peers</a></li>");
		// writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() +
		// "/nodes" + "\">Nodes</a></li>");
		writer.println("        </ul>");
		writer.println("    </div>");
		writer.println("    <div id=\"body\">");
		writer.println("    <div id=\"content\">");
	}
}
