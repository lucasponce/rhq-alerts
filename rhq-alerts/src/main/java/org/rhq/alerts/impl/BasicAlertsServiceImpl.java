package org.rhq.alerts.impl;

import org.rhq.alerts.api.common.condition.Alert;
import org.rhq.alerts.api.common.event.Metric;
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
    
    private final Timer wakeUpTimer;
    private TimerTask cepTask;    
    
    @Inject
    CepEngine cep;
    
    @Inject
    DefinitionsService definitions;

    @Inject
    NotificationsService notificationsService;

    public BasicAlertsServiceImpl() {
        LOG.debug("Creating INSTANCE...");
        
        pending = new CopyOnWriteArrayList();
        processed = new CopyOnWriteArrayList();
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
    public Collection<Alert> checkAlert() {
        return Collections.unmodifiableCollection(alerts);
    }
    
    
    @Override
    public void updateTrigger(Trigger t) {
        if (t != null) {
            for (Object fact : cep.getFacts(t)) {
                Trigger trigger = (Trigger)fact;
                if (trigger.getId().equals(t.getId())) {
                    cep.removeFact(trigger);
                    cep.addFact(t);
                    return;
                }
            }
        }
    }

    @Override
    public void removeTrigger(Trigger t) {
        if (t != null) {
            cep.removeFact(t);
        }
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

        Collection<Trigger> triggers = definitions.getTriggers();
        cep.addFacts(triggers);

        cep.addGlobal("notificationsService", notificationsService);
        cep.addGlobal("alerts", alerts);

        cepTask = new CepInvoker();
        wakeUpTimer.schedule(cepTask, DELAY, PERIOD);
    }

    @Override
    public void clear() {
        cepTask.cancel();
        
        cep.clear();
    
        pending.clear();
        alerts.clear();
        processed.clear();

        Collection<Trigger> triggers = definitions.getTriggers();
        cep.addFacts(triggers);
        
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
