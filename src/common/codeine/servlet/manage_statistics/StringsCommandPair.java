package codeine.servlet.manage_statistics;

import java.util.Comparator;

@SuppressWarnings("unused")
public class StringsCommandPair {
	private long startTime;
	private String project, command_name, command_id;
	
	public StringsCommandPair(String project, String command_name, String command_id, long startTime) {
		this.project = project;
		this.command_name = command_name;
		this.command_id = command_id;
		this.startTime = startTime;
	}
	
	public static class CommandComparator implements Comparator<StringsCommandPair> {
		@Override
		public int compare(StringsCommandPair arg0, StringsCommandPair arg1) {
			return arg0.startTime == arg1.startTime ? 0 : arg0.startTime < arg1.startTime ? 1 : -1;
		}
	}
}