package org.rhq.alerts.impl;

import org.rhq.alerts.api.services.NotificationsService;
import org.rhq.alerts.msg.common.MetricMessage;
import org.rhq.msg.common.ConnectionContextFactory;
import org.rhq.msg.common.Endpoint;
import org.rhq.msg.common.MessageId;
import org.rhq.msg.common.MessageProcessor;
import org.rhq.msg.common.producer.ProducerConnectionContext;
import org.rhq.notifiers.common.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.jms.JMSException;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import java.util.HashMap;
import java.util.Map;

/**
 TODO
 */
@Singleton
public class BasicNotificationsServiceImpl implements NotificationsService {
    private static final Logger LOG = LoggerFactory.getLogger(BasicNotificationsServiceImpl.class);
    private final static String NOTIFICATION_TOPIC = "NotificationsTopic";    
    
    @Resource(lookup = "/ConnectionFactory")
    private TopicConnectionFactory tconFactory;
    private ConnectionContextFactory ccf;
    private ProducerConnectionContext pcc;
    
    
    public BasicNotificationsServiceImpl() {
        LOG.info("Creating INSTANCE...");
    }
    
    @PostConstruct
    public void init() {
        try {
            ccf = new ConnectionContextFactory(tconFactory);
            pcc = ccf.createProducerConnectionContext(new Endpoint(Endpoint.Type.TOPIC, NOTIFICATION_TOPIC));
        } catch (JMSException e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    @Override
    public void notify(String notifierId, String msg) {
        LOG.info("Here we should notify to " + notifierId + " with MSG: " + msg);
        NotificationMessage notMsg = new NotificationMessage(msg);
        try {
            MessageId mid = new MessageProcessor().send(pcc, notMsg, notifierFilter(notifierId));
            LOG.info("Notification message sent. MessageId = " + mid);
        } catch (JMSException e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    private static Map<String, String> notifierFilter(String notifierId) {
        Map<String, String> map = new HashMap<String, String>(1);
        map.put("NotifierId", notifierId);
        return map;
    }
}
