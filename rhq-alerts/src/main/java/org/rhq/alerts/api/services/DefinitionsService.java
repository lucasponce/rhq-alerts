package org.rhq.alerts.api.services;

import org.rhq.alerts.api.common.condition.ThresholdCondition;
import org.rhq.alerts.api.common.trigger.Trigger;

import java.util.Collection;
import java.util.Map;

/**
 * Interface that allow to compose alerts definitions.
 * Alerts definitions are composed by:
 * - Conditions
 * - Triggers
 * - Rules
 */
public interface DefinitionsService {
    
    void addThreshold(ThresholdCondition threshold);
    void removeThreshold(String triggerId, String metricId);
    Collection<ThresholdCondition> getThresholds();
    ThresholdCondition getThreshold(String triggerId, String metricId);
    
    void addTrigger(Trigger trigger);
    void removeTrigger(String triggerId);
    Collection<Trigger> getTriggers();
    Trigger getTrigger(String triggerId);
    
    void addRule(String ruleId, String rule);
    void removeRule(String ruleId);
    String getRule(String ruleId);
    Map<String, String> getRules();

    /**
     * Reloads all definitions from init file.
     */
    void reload();

    void clear();
}
