package codeine.jsons.collectors;

import codeine.model.Result;

@SuppressWarnings("unused")
public class CollectorExecutionInfoWithResult {

	private CollectorExecutionInfo info;
	private Result result;

	public CollectorExecutionInfoWithResult(CollectorExecutionInfo info, Result result) {
		this.info = info;
		this.result = result;
	}

}
