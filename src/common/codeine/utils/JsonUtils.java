package codeine.utils;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class JsonUtils {

	public static <T> T cloneJson(T Json, Type clazz) {
		return new Gson().fromJson(new Gson().toJson(Json), clazz);
	}
}
