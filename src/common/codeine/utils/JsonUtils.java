package codeine.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

public class JsonUtils {

	public static <T> T cloneJson(T Json, Type clazz) {
		return new Gson().fromJson(new Gson().toJson(Json), clazz);
	}

	public static <T> T fromJsonFromFile(String file, Type clazz) {
		Gson gson = new Gson();
		try {
			BufferedReader buffered = new BufferedReader(new FileReader(file));
			T res = gson.fromJson(buffered, clazz);
			buffered.close();
			return res;
		} catch (IOException e) {
			throw ExceptionUtils.asUnchecked(e);
		}

	}
	public static <T> T fromJsonFromFile(String file, Class<T> clazz) {
		Gson gson = new Gson();
		try {
			BufferedReader buffered = new BufferedReader(new FileReader(file));
			T res = gson.fromJson(buffered, clazz);
			buffered.close();
			return res;
		} catch (IOException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}
}
