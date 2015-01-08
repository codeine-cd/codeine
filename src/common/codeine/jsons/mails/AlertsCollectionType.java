package codeine.jsons.mails;

import java.util.concurrent.TimeUnit;

public enum AlertsCollectionType {
	NotCollected(TimeUnit.NANOSECONDS, null),
	Immediately(TimeUnit.SECONDS, NotCollected),
	Hourly(TimeUnit.HOURS, Immediately),
	Daily(TimeUnit.DAYS, Hourly)
	;
	
	private TimeUnit type;
	private AlertsCollectionType previousType;
	
	private AlertsCollectionType(TimeUnit type, AlertsCollectionType previousType)
	{
		this.type = type;
		this.previousType = previousType;
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
	public AlertsCollectionType previousType() {
		return previousType;
	}
}
