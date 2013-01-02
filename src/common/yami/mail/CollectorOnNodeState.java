package yami.mail;

import java.util.List;

import yami.model.Result;
import yami.utils.LimitedQueue;

public class CollectorOnNodeState
{
	private static final int NUMBER_OF_RESULTS_TO_KEEP = 3;
	private boolean previousState = true;
	private List<Result> results = new LimitedQueue<Result>(NUMBER_OF_RESULTS_TO_KEEP);
	
	public void addResult(Result r)
	{
		previousState = calcState();
		results.add(r);
	}

	private boolean calcState()
	{
		int success = 0;
		if(results.isEmpty())
		{
			return true;
		}
		for (Result r : results)
		{
			//TODO yshabi - check if/why there are null results in the list
			if (r != null && r.success())
			{
				success++;
			}
		}
		return success > results.size() / 2;
	}

	public boolean state()
	{
		return calcState();
	}

	public boolean prevState()
	{
		return previousState;
	}

	public Result getLast()
	{
		return results.get(results.size() - 1);
	}

	@Override
	public String toString()
	{
		return "CollectorOnNodeState [previousState=" + previousState + ", results=" + results + "]";
	}
}
