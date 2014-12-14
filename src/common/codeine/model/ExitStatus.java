package codeine.model;

public class ExitStatus {
	public final static int SUCCESS = 0;
	public final static int TIMEOUT = -1;
	public final static int INTERRUPTED = -2;
	public final static int EXCEPTION = -3;
	public static final int IO_ERROR = -4;
	
	public static String fromInt(int exitStatus) {
		switch (exitStatus)	{
		case ExitStatus.TIMEOUT: {
			return "timeout";
		}
		case ExitStatus.INTERRUPTED: {
			return "interrupted";
		}
		case ExitStatus.EXCEPTION: {
			return "internal error";
		}
		case ExitStatus.IO_ERROR: {
			return "IO Error";
		}
		default: 
			return "Definition Missing for ExitStatus";
		}
	}
}
