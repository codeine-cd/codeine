package codeine;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.tz.FixedDateTimeZone;
import org.junit.Test;

import codeine.jsons.mails.AlertsCollectionType;

import com.google.common.collect.Lists;

public class CollectionTypeGetterTest {

	private DateTime dateTime = new DateTime(0, FixedDateTimeZone.UTC);
	private CollectionTypeGetter tested = new CollectionTypeGetter(FixedDateTimeZone.UTC);
	
	@Test
	public void testImmediatlyOn6Min() {
		assertEquals(immediatly(), tested.getCollectionType(dateTime.plus(TimeUnit.MINUTES.toMillis(59))));
	}
	private ArrayList<AlertsCollectionType> immediatly() {
		return Lists.newArrayList(AlertsCollectionType.Immediatly);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testIllegal() {
		assertEquals(immediatly(), tested.getCollectionType(dateTime));
	}
	@Test
	public void testHourlyOn0to5MinutesOfTheHour() {
		assertEquals(hourly(), tested.getCollectionType(dateTime.plus(TimeUnit.MINUTES.toMillis(61))));
	}
	private ArrayList<AlertsCollectionType> hourly() {
		return Lists.newArrayList(AlertsCollectionType.Immediatly, AlertsCollectionType.Hourly);
	}
	@Test
	public void testNotCollectingHourlyTwice() {
		assertEquals(hourly(), tested.getCollectionType(dateTime.plus(TimeUnit.MINUTES.toMillis(61))));
		assertEquals(immediatly(), tested.getCollectionType(dateTime.plus(TimeUnit.MINUTES.toMillis(62))));
	}
	@Test
	public void testDailyOn7to8InTheMorningOnce() {
		assertEquals(daily(), tested.getCollectionType(dateTime.plus(TimeUnit.HOURS.toMillis(7))));
		assertEquals(immediatly(), tested.getCollectionType(dateTime.plus(TimeUnit.HOURS.toMillis(7)+1)));
	}
	private ArrayList<AlertsCollectionType> daily() {
		return Lists.newArrayList(AlertsCollectionType.Immediatly, AlertsCollectionType.Hourly, AlertsCollectionType.Daily);
	}

}
