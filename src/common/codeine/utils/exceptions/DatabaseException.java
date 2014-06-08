package codeine.utils.exceptions;

import java.util.Arrays;

public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DatabaseException(String query, String target, Throwable cause, String[] args) {
		super(createMessage(query, target, cause, args), cause);
	}

	private static String createMessage(String query, String target, Throwable cause, String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("Error on '");
		sb.append(target);
		sb.append("'  caused by '");
		sb.append(cause.getMessage());
		sb.append("' while executing '");
		sb.append(query);
		sb.append("'");
		if (null != args && args.length > 0) {
			sb.append(" with args ");
			sb.append(Arrays.asList(args));
		}
		return sb.toString();
	}

	
}
