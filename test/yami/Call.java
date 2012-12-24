package yami;

import yami.configuration.*;
import yami.mail.*;

public class Call
{
	public HttpCollector c;
	public Node n;
	public CollectorOnNodeState state;
	
	public Call(HttpCollector c, Node n, CollectorOnNodeState state)
	{
		this.c = c;
		this.n = n;
		this.state = state;
	}
}