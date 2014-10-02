package codeine.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketUtils {

	private static final Logger log = Logger.getLogger(SocketUtils.class);

	public static void sendToPort(String hostPort, String value) {
		Socket socket = null;
		OutputStreamWriter osw;
		String[] splittd = hostPort.split(":");
		try {
			socket = new Socket(splittd[0], Integer.valueOf(splittd[1]));
			osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
			osw.write(value, 0, value.length());
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
