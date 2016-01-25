package codeine.collectors;

import codeine.SnoozeKeeper;
import codeine.jsons.collectors.CollectorInfo;
import codeine.model.Result;
import com.google.common.cache.LoadingCache;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutionException;

/**
 * Created by rezra3 on 1/25/2016.
 */
public class NotificationChecker {

    private static final Logger log = Logger.getLogger(NotificationChecker.class);

    public boolean shouldSendNotification(SnoozeKeeper snoozeKeeper, CollectorInfo collectorInfo, String projectName, String nodeName, LoadingCache<Long, Object> notificationsCount, Result result, Result previousResult) {
        if (snoozeKeeper.isSnooze(projectName, nodeName)) {
            log.info("in snooze period");
            return false;
        }
        if (collectorInfo.notification_enabled() && shouldNotify(result, previousResult)) {
            try {
                notificationsCount.get(System.currentTimeMillis());
            } catch (ExecutionException e) {
                log.warn("could not get from cache", e);
            }
            log.info("should send notification on " + collectorInfo.name());
            return true;
        }
        log.info("should not send notification on " + collectorInfo.name());
        return false;
    }

    private boolean shouldNotify(Result result, Result previousResult) {
        if (previousResult == null) {
            return result != null && result.exit() != 0;
        }
        else {
            return null != result
                    && result.exit() != 0 && result.exit() != previousResult.exit();
        }
    }
}
