package codeine.utils;

public class ReflectionUtils {

	public static <T> T newInstance(Class<T> clazz){
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}

}
