package yami;

import java.util.List;

import yami.configuration.CollectorRule;
import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.mail.CollectorOnNodeState;
import yami.model.IDataStore;

public class ShouldSendMailValidator
{
	private HttpCollector col;
	private Node node;
	private IDataStore ds;
	private CollectorOnNodeState state;
	private List<MailPolicy> policies;
	
	public ShouldSendMailValidator(HttpCollector c, Node n, CollectorOnNodeState st, List<MailPolicy> p, IDataStore d)
	{
		col = c;
		node = n;
		ds = d;
		state = st;
		policies = p;
	}
	
	private boolean shouldMailByPolicies(List<MailPolicy> policies, CollectorOnNodeState state)
	{
		if (null == state || null == policies)
		{
			return false;
		}
		for (MailPolicy mailPolicy2 : policies)
		{
			if (mailPolicy2.isActive(state.prevState(), state.state()))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean shouldMailByState(HttpCollector c, Node n, IDataStore d)
	{
		for (HttpCollector master : c.dependsOn())
		{
			CollectorOnNodeState r = d.getResult(n, master);
			if (r == null || !r.state())
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean shouldMail()
	{
		List<MailPolicy> policies = this.policies;
		for (CollectorRule rule : col.rules) // overwrite mailing policies if there are explicit rules
		{
			if (rule.mailPolicy != null && rule.shouldApplyForNode(node))
			{
				policies = rule.mailPolicy;
			}
		}
		return shouldMailByPolicies(policies, state) && shouldMailByState(col, node, ds);
	}
}
