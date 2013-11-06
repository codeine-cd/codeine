package codeine.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * @author oshai
 */
public class TextFileUtils {
	transient private static Logger log = Logger.getLogger(TextFileUtils.class);

	/**
	 * Fetch the entire contents of a text file, and return it in a String. This
	 * style of implementation does not throw Exceptions to the caller.
	 * 
	 * @param aFile
	 *            is a file which already exists and can be read.
	 */
	public static String getContents(String file) {
		File aFile = new File(file);
		StringBuilder contents = new StringBuilder();
		try {
			FileReader input = new FileReader(aFile);
			try {
				int line;
				char[] buf = new char[1024];
				while ((line = input.read(buf)) != -1) {
					contents.append(buf, 0, line);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return contents.toString();
	}

	/**
	 * Change the contents of text file in its entirety, overwriting any
	 * existing text.
	 * 
	 * This style of implementation throws all exceptions to the caller.
	 * 
	 * @param aFile
	 *            is an existing file which can be written to.
	 * @throws IllegalArgumentException
	 *             if param does not comply.
	 * @throws FileNotFoundException
	 *             if the file does not exist.
	 * @throws IOException
	 *             if problem encountered during write.
	 */
	public static void setContents(String aFile, String aContents) {
		setContents(new File(aFile), aContents, false);
	}

	public static void setContents(File aFile, String aContents) {
		setContents(aFile, aContents, false);
	}

	public static void setContents(File aFile, String aContents, boolean append) {
		try {
			if (!aFile.exists()) {
				aFile.createNewFile();
			}
			// use buffering
			Writer output = getWriter(aFile, append);
			try {
				// FileWriter always assumes default encoding is OK!
				output.write(aContents);
			} finally {
				output.close();
			}
		} catch (Exception ex) {
			throw ExceptionUtils.asUnchecked(ex);
		}
	}

	/**
	 * make sure to close writer after use!!!
	 */
	public static BufferedWriter getWriter(File aFile, boolean append) {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(aFile, append));
			return output;
		} catch (Exception ex) {
			throw ExceptionUtils.asUnchecked(ex);
		}
	}

	public static void getContentByLines(String file, Predicate<String> linePredicate) {
		DataInputStream in = null;
		try {
			FileInputStream fstream = new FileInputStream(file);
			in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null && linePredicate.apply(strLine)) {
			}
			// Close the input stream
		} catch (Exception ex) {
			throw ExceptionUtils.asUnchecked(ex);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException ex) {
					log.debug("getContentByLines() - ", ex);
				}
			}
		}
	}

	public static List<String> getContentFromLine(String file, int fromLine) {
		final List<String> lines = Lists.newArrayList();
		Predicate<String> linePredicate = new Predicate<String>(){
			@Override
			public boolean apply(String line){
				lines.add(line);
				return true;
			}
		};
		getContentByLines(file, linePredicate);
		if (lines.size() < fromLine) {
			return Lists.newArrayList();
		}
		return Lists.newArrayList(lines.subList(fromLine, lines.size()));
	}

}
