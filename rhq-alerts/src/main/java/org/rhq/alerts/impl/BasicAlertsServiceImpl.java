package org.rhq.alerts.impl;

import org.rhq.alerts.api.common.condition.Alert;
import org.rhq.alerts.api.common.condition.ThresholdCondition;
import org.rhq.alerts.api.common.data.Metric;
import org.rhq.alerts.api.common.data.State;
import org.rhq.alerts.api.common.trigger.Trigger;
import org.rhq.alerts.api.services.AlertsService;
import org.rhq.alerts.api.services.DefinitionsService;
import org.rhq.alerts.api.services.NotificationsService;
import org.rhq.alerts.cep.CepEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 TODO
 */
@Singleton
public class BasicAlertsServiceImpl implements AlertsService {
    private static final Logger LOG = LoggerFactory.getLogger(BasicAlertsServiceImpl.class);
    /*
        TODO These properties should be configurable
     */
    public static final int DELAY = 1000;
    public static final int PERIOD = 2000;

    private final List<Metric> pending;
    private final List<Metric> processed;
    
    private final List<Alert> alerts;
    private final List<State> states;
    
    private final Timer wakeUpTimer;
    private TimerTask cepTask;    
    
    @Inject
    CepEngine cep;
    
    @Inject
    DefinitionsService definitions;

    @Inject
    NotificationsService notificationsService;

    public BasicAlertsServiceImpl() {
        LOG.info("Creating INSTANCE...");
        
        pending = new CopyOnWriteArrayList();
        processed = new CopyOnWriteArrayList();
        states = new CopyOnWriteArrayList();
        alerts = new CopyOnWriteArrayList();

        wakeUpTimer = new Timer("BasicAlertsServiceImpl-Timer");
    }
    
    @PostConstruct
    public void init() {
        reload();
    }
    
    @Override
    public void sendMetric(Metric metric) {
        if (metric == null) {
            throw new IllegalArgumentException("Metric must be non null");
        }
        pending.add(metric);        
    }

    @Override
    public void sendMetric(Collection<Metric> metrics) {
        if (metrics == null) {
            throw new IllegalArgumentException("Metric must be non null");
        }
        pending.addAll(metrics);
    }

    @Override
    public Collection<State> checkState() {
        return Collections.unmodifiableCollection(states);
    }

    @Override
    public Collection<Alert> checkAlert() {
        return Collections.unmodifiableCollection(alerts);
    }

    @Override
    public void reload() {
        cep.reset();
        if (cepTask != null) {
            cepTask.cancel();
        }
        
        Map<String, String> rules = definitions.getRules();
        for (String ruleId : rules.keySet()) {
            cep.addRule(ruleId, rules.get(ruleId));
        }
        
        Collection<ThresholdCondition> thresholds = definitions.getThresholds();
        cep.addFacts(thresholds);
        
        Collection<Trigger> triggers = definitions.getTriggers();
        cep.addFacts(triggers);

        cep.addGlobal("notificationsService", notificationsService);
        cep.addGlobal("alerts", alerts);
        cep.addGlobal("states", states);
        
        cepTask = new CepInvoker();
        wakeUpTimer.schedule(cepTask, DELAY, PERIOD);
    }

    @Override
    public void clear() {
        cepTask.cancel();
        
        cep.clear();
    
        pending.clear();
        states.clear();
        alerts.clear();
        processed.clear();
    
        cepTask = new CepInvoker();
        wakeUpTimer.schedule(cepTask, DELAY, PERIOD);
    }
    
    /**
        TODO
     */
    public class CepInvoker extends TimerTask {
        @Override
        public void run() {
            if (!pending.isEmpty()) {
                for (Metric metric : pending) {
                    LOG.info("Adding to CEP... " + metric);
                    cep.addFact(metric);
                    processed.add(metric);
                }
                
                pending.clear();
                
                try {
                    cep.fire();    
                } catch (Exception e) {
                    LOG.error("Error on CEP processing: " + e.getMessage(), e);                    
                }
            }
        }
    }
}
