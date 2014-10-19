package codeine.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

public class RequestBodyReader {

	private String body = null;
	
	public String readBody(HttpServletRequest request) {
		if (body != null) {
			return body;
		}
		try {
			StringBuilder status = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String inputLine;
	
			while ((inputLine = in.readLine()) != null) {
				status.append(inputLine);
			}
			in.close();
			body = status.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return body;
	}

}
