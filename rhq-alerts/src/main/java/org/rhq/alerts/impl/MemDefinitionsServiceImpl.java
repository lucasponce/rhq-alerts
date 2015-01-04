package org.rhq.alerts.impl;

import org.rhq.alerts.api.common.condition.ThresholdCondition;
import org.rhq.alerts.api.common.trigger.Trigger;
import org.rhq.alerts.api.common.trigger.TriggerTemplate;
import org.rhq.alerts.api.services.DefinitionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 An implementation of {@link DefinitionsService} based on memory.
 
 Initialization is based on files located on folder:
 - META-INF/org.rhq.alert.impl.org.rhq.alerts/
 
 With the following structure:
 - rules/ folder will store a rule per file, having name of the file as ruleId and content of the file as rule
 
 - triggers.txt text file will store a trigger per line with format:
    line N: <triggerId>,<active>,<name>,<description>,<match>,<notifierId1>|<notifierId2>|<notifierId3>|...
 
 - thresholds.txt text file will store a threshold per line with format:
    line N: <triggerId>,<metricId>,<conditionSetSize>,<conditionSetIndex>,<operator>,<threshold>
 
 */
@Singleton
public class MemDefinitionsServiceImpl implements DefinitionsService {
    private static final Logger LOG = LoggerFactory.getLogger(MemDefinitionsServiceImpl.class);
    private static final String JBOSS_DATA_DIR = "jboss.server.data.dir";
    private static final String INIT_FOLDER = "org.rhq.alerts";
    
    private Map<ThresholdKey, ThresholdCondition> thresholds = new HashMap();
    private Map<String, Trigger> triggers = new HashMap();
    private Map<String, String> rules = new HashMap();

    public MemDefinitionsServiceImpl() {
        LOG.info("Creating INSTANCE...");
    }
    
    @PostConstruct
    public void init() {
        String data = System.getProperty(JBOSS_DATA_DIR);
        if (data == null) {
            LOG.error(JBOSS_DATA_DIR + " folder is null");
            return;
        }
        String folder = data + "/" + INIT_FOLDER;
        initFiles(folder);
    }
    
    @Override
    public void addThreshold(ThresholdCondition threshold) {
        if (threshold == null 
                || threshold.getTriggerId() == null 
                || threshold.getMetricId() == null) {
            throw new IllegalArgumentException("Threshold must not be null");
        }
        ThresholdKey key = new ThresholdKey();
        key.triggerId = threshold.getTriggerId();
        key.metricId = threshold.getMetricId();
        thresholds.put(key, threshold);
    }

    @Override
    public void removeThreshold(String triggerId, String metricId) {
        if (triggerId == null || metricId == null) {
            throw new IllegalArgumentException("triggerId or metric Id must not be null");
        }
        ThresholdKey key = new ThresholdKey();
        key.triggerId = triggerId;
        key.metricId = metricId;
        thresholds.remove(key);
    }

    @Override
    public Collection<ThresholdCondition> getThresholds() {
        return Collections.unmodifiableCollection(thresholds.values());
    }

    @Override
    public ThresholdCondition getThreshold(String triggerId, String metricId) {
        if (triggerId == null || metricId == null) {
            throw new IllegalArgumentException("triggerId or metric Id must not be null");
        }
        ThresholdKey key = new ThresholdKey();
        key.triggerId = triggerId;
        key.metricId = metricId;
        return thresholds.get(key);
    }

    @Override
    public void addTrigger(Trigger trigger) {
        if (trigger == null || trigger.getId() == null) {
            throw new IllegalArgumentException("Trigger must not be null");
        }
        triggers.put(trigger.getId(), trigger);
    }

    @Override
    public void removeTrigger(String triggerId) {
        if (triggerId == null) {
            throw new IllegalArgumentException("triggerId must not be null");
        }
        triggers.remove(triggerId);
    }

    @Override
    public Collection<Trigger> getTriggers() {
        return Collections.unmodifiableCollection(triggers.values());
    }

    @Override
    public Trigger getTrigger(String triggerId) {
        if (triggerId == null) {
            throw new IllegalArgumentException("triggerId must not be null");
        }        
        return triggers.get(triggerId);
    }

    @Override
    public void addRule(String ruleId, String rule) {
        if (ruleId == null || rule == null) {
            throw new IllegalArgumentException("ruleId or rule must not be null");
        }
        rules.put(ruleId, rule);
    }

    @Override
    public void removeRule(String ruleId) {
        if (ruleId == null) {
            throw new IllegalArgumentException("ruleId must not be null");            
        }
        rules.remove(ruleId);
    }

    @Override
    public String getRule(String ruleId) {
        if (ruleId == null) {
            throw new IllegalArgumentException("ruleId must not be null");
        }        
        return rules.get(ruleId);
    }

    @Override
    public Map<String, String> getRules() {
        return Collections.unmodifiableMap(rules);
    }

    @Override
    public void reload() {
        init();
    }

    @Override
    public void clear() {
        thresholds.clear();
        triggers.clear();
        rules.clear();
    }

    private void initFiles(String folder) {
        
        if (folder == null) {
            LOG.error("initFolder must not be null");
            return;
        }

        File initFolder = new File(folder);
        
        /*
            Rules initialization
         */
        File rules = new File(initFolder, "rules");
        if (rules.exists() && rules.isDirectory()) {
            for (File rule : rules.listFiles()) {
                String ruleId = rule.getName();
                try {
                    String ruleContent = new String(Files.readAllBytes(Paths.get(rule.toURI())), "UTF-8");
                    this.rules.put(ruleId, ruleContent);
                } catch (IOException e) {
                    LOG.error(e.toString(), e);    
                }
            }
        } else {
            LOG.error("rules folder not found. Skipping rules initialization.");
        }
        
        /*
            Triggers
         */
        File triggers = new File(initFolder, "triggers.txt");
        if (triggers.exists() && triggers.isFile()) {
            List<String> lines = null;
            try {
                lines = Files.readAllLines(Paths.get(triggers.toURI()), Charset.forName("UTF-8"));
            } catch (IOException e) {
                LOG.error(e.toString(), e);
            }
            if (lines != null && !lines.isEmpty()) {
                for (String line : lines) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    String[] fields = line.split(",");
                    if (fields.length == 6) {
                        String triggerId = fields[0]; 
                        boolean active = new Boolean(fields[1]).booleanValue();
                        String name = fields[2];
                        String description = fields[3];
                        TriggerTemplate.Match match = TriggerTemplate.Match.valueOf(fields[4]);
                        String[] notifiers = fields[5].split("\\|");
                        
                        Trigger trigger = new Trigger(triggerId, name, active);
                        trigger.setDescription(description);
                        trigger.setMatch(match);
                        for (String notifier : notifiers) {
                            trigger.addNotifier(notifier);
                        }
                        this.triggers.put(triggerId, trigger);
                    }
                }
            }
        } else {
            LOG.error("triggers.txt file not found. Skipping triggers initialization.");
        }
        
        /*
            Thresholds
         */
        File thresholds = new File(initFolder, "thresholds.txt");
        if (thresholds.exists() && thresholds.isFile()) {
            List<String> lines = null;
            try {
                lines = Files.readAllLines(Paths.get(thresholds.toURI()), Charset.forName("UTF-8"));
            } catch (IOException e) {
                LOG.error(e.toString(), e);
            }
            if (lines != null && !lines.isEmpty()) {
                for (String line : lines) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    String[] fields = line.split(",");
                    if (fields.length == 6) {
                        String triggerId = fields[0];
                        String metricId = fields[1];
                        int conditionSetSize = new Integer(fields[2]).intValue();
                        int conditionSetIndex = new Integer(fields[3]).intValue();
                        ThresholdCondition.Operator operator = ThresholdCondition.Operator.valueOf(fields[4]);
                        Double value = new Double(fields[5]).doubleValue();
                        
                        ThresholdCondition threshold = new ThresholdCondition(triggerId, metricId, conditionSetSize, 
                                conditionSetIndex, operator, value);
                        ThresholdKey key = new ThresholdKey();
                        key.triggerId = triggerId;
                        key.metricId = metricId;
                        this.thresholds.put(key, threshold);
                    }
                }
            }            
        } else {
            LOG.error("thresholds.txt file not found. Skipping thresholds initialization.");
        }
    }
    
    class ThresholdKey {
        public String triggerId;
        public String metricId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ThresholdKey)) return false;

            ThresholdKey that = (ThresholdKey) o;

            if (metricId != null ? !metricId.equals(that.metricId) : that.metricId != null) return false;
            if (triggerId != null ? !triggerId.equals(that.triggerId) : that.triggerId != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = triggerId != null ? triggerId.hashCode() : 0;
            result = 31 * result + (metricId != null ? metricId.hashCode() : 0);
            return result;
        }
    }
    
}
