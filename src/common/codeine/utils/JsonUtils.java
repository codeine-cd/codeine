package codeine.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;

public class JsonUtils {

	public static <T> T cloneJson(T Json, Type clazz) {
		return new Gson().fromJson(new Gson().toJson(Json), clazz);
	}

	public static <T> T fromJsonFromFile(String file, Type clazz) {
		Gson gson = new Gson();
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader buffered = new BufferedReader(fileReader);
			return gson.fromJson(buffered, clazz);
		} catch (IOException e) {
			throw ExceptionUtils.asUnchecked(e);
		}

	}
	public static <T> T fromJsonFromFile(String file, Class<T> clazz) {
		Gson gson = new Gson();
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader buffered = new BufferedReader(fileReader);
			return gson.fromJson(buffered, clazz);
		} catch (IOException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
		
	}
}
