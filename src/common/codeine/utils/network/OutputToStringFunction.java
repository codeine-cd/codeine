package codeine.utils.network;

import com.google.common.base.Function;

public final class OutputToStringFunction implements Function<String, Void> {
	private final StringBuilder $;

	OutputToStringFunction(StringBuilder $) {
		this.$ = $;
	}

	@Override
	public Void apply(String input) {
		$.append(input + "\n");
		return null;
	}
}