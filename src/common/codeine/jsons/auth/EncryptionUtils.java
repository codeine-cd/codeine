package codeine.jsons.auth;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import codeine.utils.ExceptionUtils;

import com.google.common.base.Charsets;

public class EncryptionUtils{
	private static byte[] encrypt(String key, String value) {

		byte[] raw = key.getBytes(Charsets.US_ASCII);
		if (raw.length != 16) {
			throw ExceptionUtils.asUnchecked(new IllegalArgumentException("Invalid key size."));
		}

		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
			return cipher.doFinal(value.getBytes(Charsets.US_ASCII));
		} catch (Exception e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}
	
	public static String encryptToken(String token, String secret) {
		return new String(new BASE64Encoder().encode(encrypt(secret, token)));
	}
	public static String decrypt(String secret, String encrypted) {

		byte[] raw = secret.getBytes(Charsets.US_ASCII);
		if (raw.length != 16) {
			throw ExceptionUtils.asUnchecked(new IllegalArgumentException("Invalid key size."));
		}
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
			byte[] original = cipher.doFinal(new BASE64Decoder().decodeBuffer(encrypted));
			return new String(original, Charsets.US_ASCII);
		} catch (Exception e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}
}