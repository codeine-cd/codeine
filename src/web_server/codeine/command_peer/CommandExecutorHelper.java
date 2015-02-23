package codeine.command_peer;

import codeine.permissions.IUserWithPermissions;

public class CommandExecutorHelper {

	public static boolean canCancel(IUserWithPermissions user, String requestUser) {
		return user.isAdministrator() || user.user().username().equals(requestUser);
	}
}
