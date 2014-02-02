package codeine.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class SerializationUtils {

	public static void toFile(String file, Object o) {
		try (OutputStream f = new FileOutputStream(file);
				OutputStream buffer = new BufferedOutputStream(f);
				ObjectOutput output = new ObjectOutputStream(buffer);) {
			output.writeObject(o);
		} catch (IOException ex) {
			throw ExceptionUtils.asUnchecked(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromFile(String file) {
		try (InputStream f = new FileInputStream(file);
				InputStream buffer = new BufferedInputStream(f);
				ObjectInput input = new ObjectInputStream(buffer);) {
			return (T) input.readObject();
		} catch (ClassNotFoundException ex) {
			throw ExceptionUtils.asUnchecked(ex);
		} catch (IOException ex) {
			throw ExceptionUtils.asUnchecked(ex);
		}
	}
}
