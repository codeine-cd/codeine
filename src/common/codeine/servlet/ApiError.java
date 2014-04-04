package codeine.servlet;

@SuppressWarnings("unused")
public class ApiError {

	private String title;
	private String message;
	private String exception;
	
	public ApiError(String title, String message, String exception) {
		this.title = title;
		this.message = message;
		this.exception = exception;
	}
	
}
