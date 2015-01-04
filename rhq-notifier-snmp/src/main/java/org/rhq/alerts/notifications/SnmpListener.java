package org.rhq.alerts.notifications;

import org.rhq.msg.common.consumer.BasicMessageListener;
import org.rhq.notifiers.common.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;

@MessageDriven(messageListenerInterface = MessageListener.class, activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "NotificationsTopic"),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "NotifierId like 'SNMP%'")
})
public class SnmpListener extends BasicMessageListener<NotificationMessage> {
    private final Logger log = LoggerFactory.getLogger(SnmpListener.class);

    protected void onBasicMessage(NotificationMessage msg) {
        log.info("===> MDB received message [{}]", msg);
    };
}
