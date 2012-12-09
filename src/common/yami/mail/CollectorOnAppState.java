package yami.mail;

import java.util.*;

import yami.model.*;
import yami.utils.*;

public class CollectorOnAppState
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
			if (r.success())
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
}
