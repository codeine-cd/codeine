package codeine.jsons.auth;

import java.io.IOException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import codeine.utils.ExceptionUtils;
import codeine.utils.StringUtils;

import com.google.common.base.Charsets;

public class CodeineUser {

	private static final String CODEINE_API_TOKEN_SECRET_KEY = "CodeineSecretKey";
	private String username;
	private String credentials;
	private String api_token;
	private transient  String decoded_api_token;
	
	private CodeineUser(String username, String credentials) {
		this.username = username;
		this.credentials = credentials;
		this.api_token = encryptToken(UUID.randomUUID().toString());
	}
	
	public static CodeineUser createNewUser(String username, String credentials) {
		return new  CodeineUser(username, credentials);
	}
	
	private static String encryptToken(String token)
	{
		return new String(new BASE64Encoder().encode(encrypt(CODEINE_API_TOKEN_SECRET_KEY, token)));
	}
	
	public String username() {
		return username;
	}
	
	public String credentials() {
		return credentials;
	}
	
	public String api_token() {
		if (StringUtils.isEmpty(decoded_api_token)) {
			try {
				decoded_api_token =  decrypt(CODEINE_API_TOKEN_SECRET_KEY,new BASE64Decoder().decodeBuffer(api_token));
			} catch (IOException e) {
				throw ExceptionUtils.asUnchecked(e);
			}
		}
		return decoded_api_token;
	}
	
	private static byte[] encrypt(String key, String value) {

		    byte[] raw = key.getBytes(Charsets.US_ASCII);
		    if (raw.length != 16) {
		    	throw ExceptionUtils.asUnchecked(new IllegalArgumentException("Invalid key size."));
		    }

		    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		    try 
		    {
			    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			    cipher.init(Cipher.ENCRYPT_MODE, skeySpec,new IvParameterSpec(new byte[16]));
			    return cipher.doFinal(value.getBytes(Charsets.US_ASCII));
		    } catch(Exception e) {
		    	throw ExceptionUtils.asUnchecked(e);
		    }
		  }

	private static String decrypt(String key, byte[] encrypted) {

	    byte[] raw = key.getBytes(Charsets.US_ASCII);
	    if (raw.length != 16) {
	    	throw ExceptionUtils.asUnchecked(new IllegalArgumentException("Invalid key size."));
	    }
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

	    try 
	    {
		    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		    cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
		    byte[] original = cipher.doFinal(encrypted);
		    return new String(original, Charsets.US_ASCII);
	    } catch(Exception e) {
	    	throw ExceptionUtils.asUnchecked(e);
	    }
	  }
}
