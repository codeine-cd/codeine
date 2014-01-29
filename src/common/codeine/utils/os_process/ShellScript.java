package codeine.utils.os_process;

import java.nio.file.attribute.PosixFilePermission;

import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;

public class ShellScript {

	private String content;
	private String fileName;
	private String key;

	public ShellScript(String key, String content) {
		this.key = key;
		this.content = content;
		this.fileName = "/tmp/codeine" + key.hashCode();
	}

	public String create() {
		TextFileUtils.setContents(fileName, content);
		FilesUtils.setPermissions(fileName, 
				PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
				PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE,
				PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_READ
				);
		return fileName;
	}

	public void delete() {
		FilesUtils.delete(fileName);
	}

}
