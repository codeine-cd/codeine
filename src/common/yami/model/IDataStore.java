package yami.model;

import java.util.List;

import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.mail.CollectorOnAppState;

public interface IDataStore
{

	List<MailPolicy> mailingPolicy();

	List<String> mailingList();

	CollectorOnAppState getResult(Node n, HttpCollector master);
	
}
