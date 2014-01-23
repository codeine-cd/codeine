package codeine.utils.exceptions;

public class FileReadWriteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileReadWriteException(String file, Throwable cause) {
		super("Error accessing file " + file, cause);
	}

	
}
