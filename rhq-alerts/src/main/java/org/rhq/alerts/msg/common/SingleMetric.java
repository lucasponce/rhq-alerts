package org.rhq.alerts.msg.common;

import com.google.gson.annotations.Expose;

/**
 * One single Metric.
 * This class is used to receive parsed JSON object from org.rhq.metrics.client.common.SingleMetric classes.
 * 
 * TODO Create a common lib with all classes shared between server components with proper json implemented
 */
public class SingleMetric {
    /**
     * org.rhq.metrics.client.common.SingleMetric has other fields but here we are using naming used in the toJson() method.
     */
    @Expose
    private final String id;
    
    @Expose
    private final long timestamp;
    
    @Expose
    private final Double value;

    public SingleMetric(String id, long timestamp, Double value) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SingleMetric)) return false;

        SingleMetric that = (SingleMetric) o;

        if (timestamp != that.timestamp) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SingleMetric{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
