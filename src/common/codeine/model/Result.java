package codeine.model;

public class Result
{
	public String link;
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
		return "Result [link=" + link + ", output=" + output + ", exit=" + exit + "]";
	}

	public int exit()
	{
		return exit;
	}
}
