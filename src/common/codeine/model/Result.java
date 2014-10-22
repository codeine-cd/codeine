package codeine.model;

public class Result
{
	public String output;
	private int exit;

	public Result(int exit, String output)
	{
		this.exit = exit;
		this.output = output;
	}

	public boolean success()
	{
		return exit == 0;
	}

	@Override
	public String toString()
	{
		return "Result [exit="+exit+"]";
	}
	
	public String toStringLong()
	{
		return "Result [exit=" + exit + ", output=" + output + "]";
	}

	public int exit()
	{
		return exit;
	}

	public String output() {
		return output;
	}
}
