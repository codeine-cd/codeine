package codeine.utils;

import com.google.gson.Gson;

public class JsonUtils {

	public static <T> T cloneJson(T Json, Class<T> clazz) {
		return new Gson().fromJson(new Gson().toJson(Json), clazz);
	}
}
