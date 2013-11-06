package codeine.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

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
			throw ExceptionUtils.asUnchecked(e);
		}
	}

	public static void createNewFile(String file) {
		createNewFile(new File(file));
		
	}
}
