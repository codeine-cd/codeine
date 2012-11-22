package yami.configuration;

public interface ActivePredicate
{
	public boolean isActive(boolean before, boolean after);
}
