package codeine.mail;

import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.tz.FixedDateTimeZone;

import codeine.jsons.mails.AlertsCollectionType;

import com.google.common.collect.Lists;

public class CollectionTypeGetter {

	private static final Logger log = Logger.getLogger(CollectionTypeGetter.class);
	
	private static final int TIME_WINDOW_TO_REPORT_HOUR = 5;
	private static final int TIME_WINDOW_TO_REPORT_DAY_MIN = 21;
	private static final int TIME_WINDOW_TO_REPORT_DAY_MAX = 22;
	
	private DateTime lastGet;
	private DateTime lastDailyCollection;
	private DateTime lastHourlyCollection;

	public CollectionTypeGetter() {
		this(FixedDateTimeZone.getDefault());
	}
	public CollectionTypeGetter(DateTimeZone tz) {
		lastGet = new DateTime(0, tz);
		lastHourlyCollection = new DateTime(0, tz);
		lastDailyCollection = new DateTime(0, tz);
	}

	public List<AlertsCollectionType> getCollectionType(DateTime dateTime) {
		if (!dateTime.isAfter(lastGet)){
			throw new IllegalArgumentException("collection is not after previous " + lastGet + ", " + dateTime);
		}
		lastGet = dateTime;
		if (dateTime.getHourOfDay() >= TIME_WINDOW_TO_REPORT_DAY_MIN 
				&& dateTime.getHourOfDay() <= TIME_WINDOW_TO_REPORT_DAY_MAX && 
				Hours.hoursBetween(lastDailyCollection, dateTime).getHours() > reportingWindowDay()){
			log.info("daily mail collection");
			lastDailyCollection = dateTime;
			lastHourlyCollection = dateTime;
			return Lists.newArrayList(AlertsCollectionType.Immediately, AlertsCollectionType.Hourly, AlertsCollectionType.Daily);
		}
		if (dateTime.getMinuteOfHour() <= TIME_WINDOW_TO_REPORT_HOUR && Minutes.minutesBetween(lastHourlyCollection, dateTime).getMinutes() > TIME_WINDOW_TO_REPORT_HOUR){
			log.info("hourly mail collection");
			lastHourlyCollection = dateTime;
			return Lists.newArrayList(AlertsCollectionType.Immediately, AlertsCollectionType.Hourly);
		}
		return Lists.newArrayList(AlertsCollectionType.Immediately);
	}

	private int reportingWindowDay() {
		return TIME_WINDOW_TO_REPORT_DAY_MAX - TIME_WINDOW_TO_REPORT_DAY_MIN;
	}



}
