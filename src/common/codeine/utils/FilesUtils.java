package codeine.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import codeine.utils.exceptions.FileReadWriteException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FilesUtils {

	public static void mkdirs(String path) {
		new File(path).mkdirs();
	}

	public static List<String> getFilesInDir(String dir) {
		List<String> $ = Lists.newArrayList();
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		});
		if (null == listOfFiles) {
			return Lists.newArrayList();
		}
		for (File file : listOfFiles) {
			$.add(file.getName());
		}
		return $;
	}

	public static boolean exists(String file) {
		return new File(file).exists();
	}

	public static List<File> listFiles(String dir) {
		File folder = new File(dir);
		File[] listFiles = folder.listFiles();
		if (null == listFiles){
			return Collections.emptyList();
		}
		return Arrays.asList(listFiles);
	}

	public static void createNewFile(File file) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new FileReadWriteException(file.getAbsolutePath(), e);
		}
	}

	public static void createNewFile(String file) {
		createNewFile(new File(file));
		
	}

	public static void delete(String fileName) {
		if (fileName == null) {
			return;
		}
		File file = new File(fileName);
		
		if (file.isFile()) {
			if (file.exists()) {
				file.delete();
			}
		} else {
			File[] files=file.listFiles();
			if (files == null){
				return;
			}
			for (File file2 : files) {
				delete(file2.getAbsolutePath());
			}
		  file.delete();
		}
	}

	public static void setPermissions(String fileName, PosixFilePermission... permission) {
		try {
			Files.setPosixFilePermissions(FileSystems.getDefault().getPath(fileName), Sets.newHashSet(permission));
		} 
		catch (IOException e) {
			throw new FileReadWriteException(fileName, e);
		}
	}
}
