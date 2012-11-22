package yami.model;

import java.util.List;

import yami.configuration.MailPolicy;

public interface IDataStore
{

	List<MailPolicy> mailingPolicy();

	List<String> mailingList();
	
}
