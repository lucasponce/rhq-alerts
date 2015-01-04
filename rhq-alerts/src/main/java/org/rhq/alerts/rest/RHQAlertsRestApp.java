package org.rhq.alerts.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 TODO
 */
@ApplicationPath("/api")
public class RHQAlertsRestApp extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(RHQAlertsRestApp.class);
    
    public RHQAlertsRestApp() {
        
        LOG.info("RHQ Alerts starting ...");
        
    }
    
}
