package yami.model;

import java.util.List;

import yami.configuration.HttpCollector;
import yami.configuration.Node;
import yami.configuration.Peer;
import yami.mail.CollectorOnNodeState;

public interface IDataStore
{

	CollectorOnNodeState getResult(Node n, HttpCollector master);

	void addSilentPeriod(Peer peer, long l);

	void addResults(Node node, HttpCollector collector, Result r);

	List<Node> enabledInternalNodes();
	
}
