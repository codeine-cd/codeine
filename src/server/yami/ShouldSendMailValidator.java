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
	
	private boolean shouldMailByPolicies(List<MailPolicy> policies)
	{
		if (null == state || null == policies)
		{
			return false;
		}
		for (MailPolicy p : policies)
		{
			if (p.isActive(state.prevState(), state.state()))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean shouldMailByDependencies(HttpCollector c, Node n, List<MailPolicy> policies)
	{
		for (MailPolicy policy : policies)
		{
			for (HttpCollector master : c.dependsOn())
			{
				CollectorOnNodeState r = ds.getResult(n, master);
				if (r == null || policy.isActive(r.prevState(), r.state()))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean shouldMail()
	{
		List<MailPolicy> calculatedPolicies = this.policies;
		for (CollectorRule rule : col.rules) // overwrite mailing policies if there are explicit rules
		{
			if (rule.mailPolicy != null && rule.shouldApplyForNode(node))
			{
				calculatedPolicies = rule.mailPolicy;
			}
		}
		return calculatedPolicies.contains(MailPolicy.EachRun) || (shouldMailByPolicies(calculatedPolicies) && shouldMailByDependencies(col, node, calculatedPolicies));
	}
}
