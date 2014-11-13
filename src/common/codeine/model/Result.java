package codeine.model;

public class Result {
	
	private int exit;
	private String output;
	private String outputFromFile;

	public Result(int exit, String output) {
		this(exit, output, null);
	}

	public Result(int exit, String output, String outputFromFile) {
		this.exit = exit;
		this.output = output;
		this.outputFromFile = outputFromFile;
	}

	public boolean success() {
		return exit == 0;
	}

	@Override
	public String toString() {
		return "Result [exit=" + exit + "]";
	}

	public String toStringLong() {
		return "Result [exit=" + exit + ", " + (output != null ? "output=" + output + ", " : "")
				+ (outputFromFile != null ? "outputFromFile=" + outputFromFile : "") + "]";
	}

	public int exit() {
		return exit;
	}

	public String output() {
		return output;
	}

	public void output(String output) {
		this.output = output;
	}

	public void outputFromFile(String outputFromFile) {
		this.outputFromFile = outputFromFile;
	}

	public String outputFromFile() {
		return outputFromFile;
	}
}
