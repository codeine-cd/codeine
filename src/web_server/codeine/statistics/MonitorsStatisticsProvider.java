package codeine.statistics;

import javax.inject.Inject;
import javax.inject.Provider;

public class MonitorsStatisticsProvider implements Provider<MonitorsStatistics>{
	private MonitorsStatistics monitorsStatistics;
	@Inject
	public MonitorsStatisticsProvider(MonitorsStatistics monitorsStatistics) {
		super();
		this.monitorsStatistics = monitorsStatistics;
		this.monitorsStatistics.restore();
	}
	@Override
	public MonitorsStatistics get() {
		return monitorsStatistics;
	}
	
}