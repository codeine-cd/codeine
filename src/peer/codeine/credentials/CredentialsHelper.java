package codeine.credentials;

import sun.misc.BASE64Encoder;

public class CredentialsHelper {

	public String encode(final String value1){
		BASE64Encoder encoder = new BASE64Encoder();
		return new StringBuffer(encoder.encode(value1.getBytes())).reverse().toString().replace("\n", "");
	}
}
