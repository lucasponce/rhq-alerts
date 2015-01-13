package org.rhq.alerts.impl;

import org.rhq.alerts.api.common.condition.ThresholdCondition;
import org.rhq.alerts.api.common.trigger.Trigger;

import java.util.HashMap;
import java.util.Map;

import static org.rhq.alerts.impl.RulesGenerator.RulesTemplate.NL;
import static org.rhq.alerts.impl.RulesGenerator.RulesTemplate.HEADER;

/**
 TODO 
 */
public class RulesGenerator {

    public Map<String, String> generateRules(ThresholdCondition threshold) {
        Map<String, String> rules = new HashMap<String, String>();
        
        if (threshold != null && threshold.getTriggerId() != null && threshold.getMetricId() != null &
                !threshold.getTriggerId().isEmpty() && !threshold.getMetricId().isEmpty()) {
            
            String thresholdRuleId = "Threshold-" + threshold.getTriggerId() + "-" + threshold.getMetricId();
            String thresholdRule = generateThresholdRule(thresholdRuleId, threshold);
            rules.put(thresholdRuleId, thresholdRule);
            
            String conditionsRuleId = "Alert" + threshold.getConditionSetSize() + "Condition";
            String conditionsRule = generateConditionRule(conditionsRuleId, threshold);
            rules.put(conditionsRuleId, conditionsRule);
        }
        
        return rules;
    }
    
    private String generateThresholdRule(String thresholdRuleId, ThresholdCondition threshold) {
        String rule = HEADER + NL + NL +
        
        "rule \"" + thresholdRuleId + "\"" + NL + NL +
                        
        "when" + NL + 
        "   $t  : Trigger( active == true, id == \"" + threshold.getTriggerId() + "\")" + NL +
        "   $m  : Metric( id == \"" + threshold.getMetricId() + "\", " + operator(threshold) + " )" + NL +
                        
        "then"  + NL +
        "   ConditionMatch cm = new ConditionMatch( \"" + threshold.getTriggerId() + "\"," +
                                    threshold.getConditionSetSize() + "," +
                                    threshold.getConditionSetIndex() + "," +
                                    " \"" + threshold.getTriggerId() + ": \" + $value + \" " + threshold.getOperator() +
                                    " " + threshold.getThreshold() + "\" );" + NL +
        "   insertLogical(cm);" + NL +
        "end" + NL;
        
        return rule;
    }
    
    private String operator(ThresholdCondition threshold) {
        String operator = "$value : value ";
        
        if (threshold.getOperator().equals(ThresholdCondition.Operator.GT)) {
            operator += " > " + threshold.getThreshold();
        } else if (threshold.getOperator().equals(ThresholdCondition.Operator.GTE)) {
            operator += " >= " + threshold.getThreshold();
        } else if (threshold.getOperator().equals(ThresholdCondition.Operator.LT)) {
            operator += " < " + threshold.getThreshold();
        } else if (threshold.getOperator().equals(ThresholdCondition.Operator.LTE)) {
            operator += " <= " + threshold.getThreshold();
        }
        
        return operator;
    }
    
    private String generateConditionRule(String conditionsRuleId, ThresholdCondition threshold) {
        String rule = HEADER + NL + NL +
        
        "rule \"" + conditionsRuleId + "\"" + NL +
        "when" + NL +
        "   $t  : Trigger( active == true, id == '" + threshold.getTriggerId() + "' )" + NL +
        conditions(threshold) +
        "then" + NL +
        "   $t.setActive( false );" + NL +
        "   Alert alert = new Alert( \"" + threshold.getTriggerId() + "\" ); " + NL +
        conditionsActions(threshold) +
        "   alerts.add( alert ); " + NL +
        "   insert( alert ); " + NL +
        "   update( $t ); " + NL +
        "end";
        
        return rule;
    }
    
    private String conditions(ThresholdCondition threshold) {
        String conditions = "";
        int setSize = threshold.getConditionSetSize();
        for (int i = 1; i <= setSize; i++) {
            conditions += "   $cm" + i +
                                  " : ConditionMatch( triggerId == '" + threshold.getMetricId() + "', " +
                                  "conditionSetSize == " + setSize + ", " +
                                  "conditionSetIndex == " + i + ")" + NL;
        }
        return conditions;
    }
    
    private String conditionsActions(ThresholdCondition threshold) {
        String conditionsActions = "";
        int setSize = threshold.getConditionSetSize();
        for (int i = 1; i <= setSize; i++) {
            conditionsActions += "   alert.addConditionMatch( $cm" + i + " );" + NL;
        }
        conditionsActions += "   for (String notifierId : $t.getNotifiers()) {" + NL;
        conditionsActions += "      notificationsService.notify( notifierId, ";
        for (int i = 1; i <= setSize; i++) {
            conditionsActions += "$cm" + i + ".getLog()";
            if (i<setSize) {
                conditionsActions += " + \" AND \" + ";
            }
        }
        conditionsActions += ");" + NL;
        conditionsActions += "   }" + NL;
        
        return conditionsActions;
    } 
    
    static class RulesTemplate {
        public static String NL = "\n";
        
        public static String HEADER =
                "package org.poc.rules" + NL + NL +
                                              
                "import org.rhq.alerts.api.common.trigger.Trigger" + NL +
                "import org.rhq.alerts.api.common.condition.ConditionMatch" + NL +
                "import org.rhq.alerts.api.common.condition.ThresholdCondition" + NL +
                "import org.rhq.alerts.api.common.condition.Alert" + NL +
                "import org.rhq.alerts.api.common.data.Metric" + NL +
                "import org.rhq.alerts.api.common.data.State" + NL +
                "import org.rhq.alerts.api.services.NotificationsService" + NL +
                "import java.util.List" + NL +

                "global NotificationsService notificationsService" + NL +
                "global List alerts" + NL +
                "global List states" + NL + NL;
    }

}
