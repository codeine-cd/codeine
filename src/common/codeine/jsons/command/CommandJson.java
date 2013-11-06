package codeine.jsons.command;


public class CommandJson 
{
    private static final int MAX_CONCURRENCY = 500;
    
	private String name;
    private String title;
    private String credentials;
    private boolean one_at_a_time;
    private int concurrency = 10;

    public String title(){
    	return title == null ? name : title;
    }

	public String name() {
		return name;
	}

	public String credentials() {
		return credentials;
	}

	public boolean one_at_a_time() {
		return one_at_a_time;
	}
	
	public int concurrency() {
		return Math.min(concurrency, MAX_CONCURRENCY);
	}
}
