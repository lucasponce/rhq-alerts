package org.rhq.alerts.api.services;

import org.rhq.alerts.api.common.condition.Alert;
import org.rhq.alerts.api.common.data.Metric;
import org.rhq.alerts.api.common.data.State;

import java.util.Collection;

/**
 * Interface that allow to send new data to alerts engine and check state
 */
public interface AlertsService {

    void sendMetric(Metric metric);
    void sendMetric(Collection<Metric> metrics);

    Collection<State> checkState();
    Collection<Alert> checkAlert();

    /**
     * Reloads all alert definitions available through {@link DefinitionsService} 
     */
    void reload();

    /**
     * Clear session state
     */
    void clear();
}
