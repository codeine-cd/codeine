package codeine.utils;

import java.util.Map.Entry;

public class SimpleEntry<K,V> implements Entry<K, V> {

	private K key;
	private V value;

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V valueOld = this.value;
		this.value = value;
		return valueOld;
	}

	public static <K,V> Entry<K, V> create(K key, V value) {
		return new SimpleEntry<K,V>(key, value);
	}

	private SimpleEntry(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Entry [key=" + key + ", value=" + value + "]";
	}

	
}
