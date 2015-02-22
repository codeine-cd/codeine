package codeine.utils;

public class MiscUtils {

	public static boolean equals(Object o1, Object o2) {
		if (null == o1 || null == o2) {
			return null == o1 && null == o2;
		}
		return o1.equals(o2);
	}
}
