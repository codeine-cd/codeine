package codeine.credentials;

import sun.misc.BASE64Encoder;

public class CredentialsHelper {

	public static String encode(final String value){
		return new StringBuffer(new BASE64Encoder().encode(value.getBytes())).reverse().toString().replace("\n", "");
	}
}
