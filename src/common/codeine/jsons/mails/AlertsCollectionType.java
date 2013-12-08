package codeine.jsons.mails;

import java.util.concurrent.TimeUnit;

public enum AlertsCollectionType {
	Daily(TimeUnit.DAYS),
	Hourly(TimeUnit.HOURS),
	Immediately(TimeUnit.SECONDS),
	Immediatly(TimeUnit.SECONDS),
	NotCollected(TimeUnit.NANOSECONDS)
	;
	
	private TimeUnit type;
	
	private AlertsCollectionType(TimeUnit type)
	{
		this.type = type;
	}
	
	public AlertsCollectionType fromInt(TimeUnit t)
	{
		for (AlertsCollectionType v : values())
		{
			if (v.toTimeUnit() == t)
			{
				return v;
			}
		}
		throw new IllegalArgumentException("No implementation for time unit " + t);
	}

	private TimeUnit toTimeUnit()
	{
		return type;
	}
	
	public long toLong() {
		return toTimeUnit().toMillis(1);
	}
}
