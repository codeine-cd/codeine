package codeine.jsons.collectors;

import codeine.model.Result;

public class CollectorExecutionInfoWithResult {

	private CollectorExecutionInfo info;
	private Result result;

	public CollectorExecutionInfoWithResult(CollectorExecutionInfo info, Result result) {
		this.info = info;
		this.result = result;
	}

	@Override
	public String toString() {
		return "CollectorExecutionInfoWithResult [info=" + info + ", result=" + result + "]";
	}

	public Result result() {
		return result;
	}

	public CollectorExecutionInfo info() {
		return info;
	}

	
}
