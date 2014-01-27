package codeine.utils.exceptions;

public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DatabaseException(String query, String target, Throwable cause) {
		super("Error on '" + target + "'  caused by '" + cause.getMessage() + "' while executing '" + query + "'" , cause);
	}

	
}
