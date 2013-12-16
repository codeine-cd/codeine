package codeine.manage;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.configuration.Links;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class ConfigureServlet extends AbstractServlet
{
	private static final Logger log = Logger.getLogger(ConfigureServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Inject private IConfigurationManager configurationManager;
	@Inject private Links links;
	@Inject private Gson gson;
	@Inject private GlobalConfigurationJson globalConfigurationJson;

	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.debug("myGet request");
		PrintWriter writer = getWriter(response);
		printConfigForm(writer);
	}

	private void printConfigForm(PrintWriter writer) {
		
		writer.write("<div id='metawidget'>\n");
		writer.write("<button onclick='save()'>Save</button>\n");
		writer.write("</div>\n");
		writer.write("<script type='text/javascript'>\n");
		writer.write("var toinspect = " + gson.toJson(globalConfigurationJson) + ";\n");
		writer.write("var mw = new metawidget.Metawidget( document.getElementById( 'metawidget' ));\n");
		writer.write("mw.toInspect = toinspect;\n");
		writer.write("mw.buildWidgets();\n");
		writer.write("function save() {\n");
		writer.write("    mw.getWidgetProcessor(\n");
		writer.write("      function( widgetProcessor ) {\n");
		writer.write("         return widgetProcessor instanceof metawidget.widgetprocessor.SimpleBindingProcessor;\n");
		writer.write("     }\n");
		writer.write("  ).save( mw );\n");
		writer.write("  console.log( mw.toInspect );\n");
		writer.write("  postJson( '"+Constants.CONFIG_SUBMIT_CONTEXT+"', mw.toInspect );\n");
		writer.write("}\n");
		writer.write("</script>\n");
//		
//		writer.write("<script type='text/javascript'>\n");
//		writer.write("$(function() {\n");
//		writer.write("var frm = $(document.input);\n");
//		writer.write("var dat = JSON.stringify(frm.serializeArray());\n");
//		writer.write("alert('I am about to POST this:\\n\\n' + dat);\n");
//		writer.write("$.post(\n");
//		writer.write("frm.attr('action'),\n");
//		writer.write("dat,\n");
//		writer.write("function(data) {\n");
//		writer.write("alert('Response: ' + data);\n");
//		writer.write(" }\n");
//		writer.write(" );\n");
//		writer.write(" });\n");
//		writer.write("</script>\n");
	}
}
