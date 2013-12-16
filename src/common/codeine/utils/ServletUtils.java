package codeine.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class ServletUtils {

	public static PrintWriter getWriter(HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return writer;
	}
}
