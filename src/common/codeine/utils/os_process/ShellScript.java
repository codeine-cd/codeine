package codeine.utils.os_process;

import java.io.File;
import java.nio.file.attribute.PosixFilePermission;

import codeine.utils.FilesUtils;
import codeine.utils.StringUtils;
import codeine.utils.TextFileUtils;

public class ShellScript {

	private String content;
	private String fileName;
//	private String key;
	private boolean windows;

	public ShellScript(String key, String content) {
		this(key, content, false, "/tmp");
	}
	public ShellScript(String key, String content, boolean windows, String tmp_dir) {
//		this.key = key;
		this.content = content;
		this.windows = windows;
		String tmpDir = StringUtils.isEmpty(tmp_dir) ? System.getProperty("java.io.tmpdir") : tmp_dir;
		this.fileName = tmpDir + File.separator + "codeine" + key.hashCode() +  (windows ? ".bat" : ".sh");
	}

	public String create() {
		content = content.replace("\n", System.lineSeparator());
		TextFileUtils.setContents(fileName, content);
		if (!windows) {
			FilesUtils.setPermissions(fileName, 
					PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
					PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE,
					PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_READ
					);
		} 
		return fileName;
	}

	public void delete() {
		FilesUtils.delete(fileName);
	}

}
