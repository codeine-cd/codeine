package codeine.utils.exceptions;

public class ConnectToDatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConnectToDatabaseException(String target, Throwable cause) {
		super("Error connecting to '" + target + "'  caused by '" + cause.getMessage() + "'" , cause);
	}

	
}
