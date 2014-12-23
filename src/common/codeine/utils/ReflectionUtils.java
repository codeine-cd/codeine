package codeine.utils;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

public class ReflectionUtils {

	private static final Logger log = Logger.getLogger(ReflectionUtils.class);

	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object obj, String sFieldName) {
		Field field = getField(obj, sFieldName);
		try {
			return (T) field.get(obj);
		} catch (IllegalArgumentException ex) {
			throw ExceptionUtils.asUnchecked(ex);
		} catch (IllegalAccessException ex) {
			throw ExceptionUtils.asUnchecked(ex);
		}
	}

	static private Field getField(Object obj, String sFieldName) {
		Class<?> c = obj.getClass();
		return getFieldForClass(c, sFieldName);
	}

	static private Field getFieldForClass(Class<?> c, String sFieldName) {
		log.debug("getFieldForClass() - called with c.getName() = " + c.getName());
		try {
			Field field = c.getDeclaredField(sFieldName);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			log.debug("getFieldForClass() - not found, will try in superclass");
			// check super
			if (null == c.getSuperclass()) {
				throw ExceptionUtils.asUnchecked(e);
			}
			return getFieldForClass(c.getSuperclass(), sFieldName);
		}
	}

}
