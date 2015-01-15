package org.rhq.alerts.api.services;

import org.rhq.alerts.api.common.condition.Alert;
import org.rhq.alerts.api.common.event.Metric;
import org.rhq.alerts.api.common.trigger.Trigger;

import java.util.Collection;

/**
 * Interface that allow to send new events to alerts engine.
 * It can check status of session's object and modify them. 
 */
public interface AlertsService {

    /*
        Send a single metric to the alerts engine.
     */
    void sendMetric(Metric metric);
    /*
        Send a collection of metrics to the alerts engine.
     */
    void sendMetric(Collection<Metric> metrics);
    
    /*
        Return alerts infered by the alerts engine.
     */
    Collection<Alert> checkAlert();

    /*
        Update a trigger from the session.
        Trigger must exist previously.
     */
    void updateTrigger(Trigger t);
    
    /*
        Remove a trigger from the session.
        Trigger must exist previously.
     */
    void removeTrigger(Trigger t);

    /**
     * Reloads all alert definitions available through {@link DefinitionsService} 
     */
    void reload();

    /**
     * Reset session state
     */
    void clear();
}
