package codeine.servlet;


public class FrontEndServletException extends Exception {

	private static final long serialVersionUID = 1L;
	private Exception inner_exception;
	private int http_status;
	
	public FrontEndServletException(Exception inner_exception, int http_status) {
		super();
		this.inner_exception = inner_exception;
		this.http_status = http_status;
	}
	
	public int http_status() {
		return http_status;
	}
	
	public Exception inner_exception() {
		return inner_exception;
	}

}
