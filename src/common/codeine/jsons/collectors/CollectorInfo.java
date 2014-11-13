package codeine.jsons.collectors;

public class CollectorInfo {

	private String name;
	private String script_content;
	private Integer minInterval;
	private String credentials;
	private CollectorType type;
	
	public static enum CollectorType {
		String,Integer,Boolean
	}
}
