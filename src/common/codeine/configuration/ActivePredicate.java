package codeine.configuration;

public interface ActivePredicate
{
	public boolean isActive(boolean before, boolean after);
}
