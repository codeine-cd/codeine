package codeine.jsons.auth;

import java.util.UUID;

import codeine.model.Constants;
import codeine.utils.StringUtils;

public class CodeineUser {

	private String username;
	private String sisma;
	private String api_token;// Encrypted
	private transient String plain_api_token;

	private CodeineUser(String username, String sisma) {
		this.username = username;
		this.sisma = sisma;
		this.api_token = EncryptionUtils.encryptToken(UUID.randomUUID().toString(),
				Constants.CODEINE_API_TOKEN_DERIVER);
	}

	public static CodeineUser createNewUser(String username, String sisma) {
		return new CodeineUser(username, sisma);
	}

	public static CodeineUser createGuestUser() {
		return new CodeineUser(Constants.GUEST_USER, Constants.GUEST_USER);
	}

	public static CodeineUser createGuest(String name) {
		return new CodeineUser(name, name);
	}

	public String username() {
		return username;
	}

	public String credentials() {
		return sisma;
	}

	public String encodedApiTokenWithTime() {
		return EncryptionUtils.encryptToken(api_token() + "#" + System.currentTimeMillis(),
				Constants.CODEINE_API_TOKEN_DERIVER);
	}

	public String api_token() {
		if (StringUtils.isEmpty(plain_api_token)) {
			plain_api_token = EncryptionUtils.decrypt(Constants.CODEINE_API_TOKEN_DERIVER, api_token);
		}
		return plain_api_token;
	}

	@Override
	public String toString() {
		return "CodeineUser [username=" + username + "]";
	}

}
