package org.rhq.alerts.msg.common;

import com.google.gson.annotations.Expose;
import org.rhq.alerts.api.common.data.Metric;
import org.rhq.msg.common.BasicMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * TODO
 */
public class MetricMessage extends BasicMessage {

    @Expose
    List<SingleMetric> metrics;
    
    protected MetricMessage() { }
    
    public MetricMessage(List<SingleMetric> metrics) {
        this.metrics = new ArrayList<SingleMetric>(metrics);
    }
    
    public MetricMessage(SingleMetric... metrics) {
        this.metrics = new ArrayList<SingleMetric>();
        if (metrics != null) {
            for (SingleMetric metric : metrics) {
                this.metrics.add(metric);
            }
        }
    }
    
    public List<Metric> getMetrics() {
        if (metrics == null) {
            return null;
        }
        ArrayList<Metric> metrics = new ArrayList();
        if (!this.metrics.isEmpty()) {
            for (SingleMetric metric : this.metrics) {
                Metric newMetric = new Metric(metric.getId(), metric.getTimestamp(), metric.getValue());
                metrics.add(newMetric);
            }
        }
        return metrics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricMessage)) return false;

        MetricMessage message = (MetricMessage) o;

        if (metrics != null ? !metrics.equals(message.metrics) : message.metrics != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return metrics != null ? metrics.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MetricMessage{" +
                "metrics=" + metrics +
                '}';
    }
}
