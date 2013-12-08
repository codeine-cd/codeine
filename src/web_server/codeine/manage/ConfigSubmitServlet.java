package codeine.manage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.configuration.Links;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.servlet.AbstractServlet;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class ConfigSubmitServlet extends AbstractServlet
{
	private static final Logger log = Logger.getLogger(ConfigSubmitServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Inject private IConfigurationManager configurationManager;
	@Inject private Links links;
	@Inject private Gson gson;
	@Inject private GlobalConfigurationJsonStore globalConfigurationJsonStore;

	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		GlobalConfigurationJson c = readBodyJson(request, GlobalConfigurationJson.class);
		log.info("storing " + c);
		globalConfigurationJsonStore.store(c);
	}
}
