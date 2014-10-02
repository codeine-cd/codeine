package codeine.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketUtils {

	private static final Logger log = Logger.getLogger(SocketUtils.class);

	public static void sendToPort(String hostPort, String value) {
		String[] splittd = hostPort.split(":");
		Socket socket = null;
		try {
			socket = new Socket(splittd[0], Integer.valueOf(splittd[1]));
			OutputStream out = socket.getOutputStream();
			out.write(value.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			throw ExceptionUtils.asUnchecked(e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				log.info("failed to close socket " + e.getMessage());
			}
		}

	}
}
