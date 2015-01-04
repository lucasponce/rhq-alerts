package org.rhq.notifiers.common;

import com.google.gson.annotations.Expose;
import org.rhq.msg.common.BasicMessage;

/**
 * TODO
 */
public class NotificationMessage extends BasicMessage {
    
    @Expose
    String message;
    
    protected NotificationMessage() {
        
    }

    public NotificationMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationMessage)) return false;

        NotificationMessage that = (NotificationMessage) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return message != null ? message.hashCode() : 0;
    }

    @Override
    public String 
    toString() {
        return "NotificationMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
