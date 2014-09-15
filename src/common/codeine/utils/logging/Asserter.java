package codeine.utils.logging;

import org.apache.log4j.Logger;

public class Asserter {

	private static final Logger log = Logger.getLogger(Asserter.class);

	public static void isFalse(boolean predicate, String message) {
		if (predicate){
			log.warn(message, new AssertionError(predicate));
		}
	}

}
