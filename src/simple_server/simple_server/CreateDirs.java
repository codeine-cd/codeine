package simple_server;

import java.io.File;

import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.ThreadUtils;

import com.google.common.base.Stopwatch;

public class CreateDirs {

	static String dir = "/nfs/site/disks/iec_sws9/standard_test/";

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 5; i < 6; i++) {
			for (int j = 0; j < 1; j++) {
				int files = 10000 * i;
				sb.append(files + " ");
				System.out.println("start " + files);
				sb.append(createdirs(files)).append(" ");
				ThreadUtils.sleep(1000);
				sb.append(createfiles(files) + " ");
				ThreadUtils.sleep(1000);
				sb.append(readfiles(files) + " ");
				ThreadUtils.sleep(1000);
				sb.append(readdir()).append(" ");
				ThreadUtils.sleep(1000);
//				sb.append(rmdirs(files)).append(" ");
				ThreadUtils.sleep(1000);
				System.out.println("done");
				sb.append("\n");
			}
		}
		System.out.println(sb.toString());
	}

	private static String createdirs(int files) {
		Stopwatch s = Stopwatch.createStarted();
		for (int i = 0; i < files; i++) {
			FilesUtils.mkdirs(dir + i);
		}
		System.out.println("createdirs took " + s);
		return s.toString();
	}

	private static String createfiles(int files) {
		Stopwatch s = Stopwatch.createStarted();
		for (int i = 0; i < files; i++) {
			TextFileUtils.setContents(dir + i + "/" + i, "1 " + i);
		}
		System.out.println("createfiles took " + s);
		return s.toString();
	}

	private static String readfiles(int files) {
		StringBuilder sb = new StringBuilder();
		Stopwatch s = Stopwatch.createStarted();
		for (int i = 0; i < files; i++) {
			sb.append(TextFileUtils.getContents(dir + i + "/" + i));
		}
		System.out.println("readfiles took " + s);
		return s.toString();
	}

	private static String readdir() {
		StringBuilder sb = new StringBuilder();
		Stopwatch s = Stopwatch.createStarted();
		sb.append(new File(dir).listFiles().length);
		System.out.println("readdir took " + s);
		System.out.println("num of files " + sb);
		return s.toString();
	}

	private static String rmdirs(int files) {
		Stopwatch s = Stopwatch.createStarted();
		for (int i = 0; i < files; i++) {
			FilesUtils.delete(dir + i + "/" + i);
			FilesUtils.delete(dir + i);
		}
		System.out.println("rmdirs took " + s);
		return s.toString();
	}

}
