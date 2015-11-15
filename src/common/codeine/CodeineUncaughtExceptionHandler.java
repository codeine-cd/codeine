package codeine;

import codeine.utils.ThreadUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.lang.Thread.UncaughtExceptionHandler;

public class CodeineUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private Logger log = Logger.getLogger(CodeineUncaughtExceptionHandler.class);

	private boolean errorPrintedToOut = false;

	public CodeineUncaughtExceptionHandler() {
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try {
			System.err.println("Uncaught exception!");
			if (!errorPrintedToOut) {
				errorPrintedToOut = true;
				e.printStackTrace();
			}
			log.error("Uncaught exception in thread " + t.getName(), e);
		} catch (Throwable tr) {
			try {
				e.printStackTrace();
				tr.printStackTrace();
			} catch (Throwable e1) {
				log.fatal("could not write stacktrace of exception " + t.getName());
				addExceptionInfo(e1);
				addExceptionInfo(e);
				addExceptionInfo(tr);
			}
		}
	}

	private void addExceptionInfo(Throwable e) {
		log.fatal("exception info " + e.getMessage());
	}

//	public static void main(String[] args) {
//
//	}
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Thread.setDefaultUncaughtExceptionHandler(new CodeineUncaughtExceptionHandler());
        ThreadUtils.newFixedThreadPool(1, "aaa").execute(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getMessage() {
                        overflow();
                        return "";
                    }

                    @Override
                    public void printStackTrace(java.io.PrintWriter s) {
                        overflow();
                    }

                    @Override
                    public void printStackTrace() {
                        overflow();
                    }

                    private void overflow() {
                        overflow();
                    }

                };
            }
        });
        /*
		throw new RuntimeException() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getMessage() {
				overflow();
				return "";
			}

			@Override
			public void printStackTrace(java.io.PrintWriter s) {
				overflow();
			}

			@Override
			public void printStackTrace() {
				overflow();
			}

			private void overflow() {
				overflow();
			}

		};
		*/
	}

}