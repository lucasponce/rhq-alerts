package org.rhq.alerts.msg.listener;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.MessageListener;

import org.rhq.alerts.api.common.event.Metric;
import org.rhq.alerts.api.services.AlertsService;
import org.rhq.alerts.msg.common.MetricMessage;
import org.rhq.msg.common.consumer.BasicMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@MessageDriven(messageListenerInterface = MessageListener.class, activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "MetricsTopic")})
public class MetricsListener extends BasicMessageListener<MetricMessage> {
    private final Logger LOG = LoggerFactory.getLogger(MetricsListener.class);

    
    @Inject
    AlertsService alerts;
    
    protected void onBasicMessage(MetricMessage msg) {
        LOG.info("===> MDB received message [{}]", msg);
        List<Metric> metrics = msg.getMetrics();
        alerts.sendMetric(metrics);
    }


}
