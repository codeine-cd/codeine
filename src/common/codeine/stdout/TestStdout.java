package codeine.stdout;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class TestStdout {


	public static void main(String[] args) {
		BasicConfigurator.configure();
		StdoutRedirectToLog.redirect();
		Logger log = Logger.getLogger(TestStdout.class);
		String message = "222222222222222222222222222\n222222222222222222222222222222222";
		log.info(message);
		System.out.println("11111111111111111111111111\n1111111111111111111111111111111");
		System.out.println("11111111111111111111111111\n1111111111111111111111111111111");
		System.out.println("11111111111111111111111111\n1111111111111111111111111111111");
		System.out.println("11111111111111111111111111\n1111111111111111111111111111111");
		printException(10);
	}

	public static void printException(int depth) {
		if (depth==0) {
			new Exception(new RuntimeException()).printStackTrace();
		}
		else {
			printException(depth-1);
		}
			
	}
}
