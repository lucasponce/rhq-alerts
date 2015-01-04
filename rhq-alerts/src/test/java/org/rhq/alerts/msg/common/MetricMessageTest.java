package org.rhq.alerts.msg.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class MetricMessageTest {
    
    @Test
    public void jsonTest() {
        SingleMetric metric1 = new SingleMetric("id1", 0, 0.0d);
        MetricMessage msg = new MetricMessage(metric1);
        String json = msg.toJSON();
        MetricMessage fromJson = MetricMessage.fromJSON(json, MetricMessage.class);
        assertTrue(msg.equals(fromJson));
    }

}