package org.rhq.alerts.rest;

import org.rhq.alerts.api.common.condition.Alert;
import org.rhq.alerts.api.services.AlertsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 TODO
 */
@Path("/")
public class AlertsHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AlertsHandler.class);
    
    @Inject
    AlertsService alertsService;
    
    @GET
    @Path("/alerts")
    @Produces(APPLICATION_JSON)
    public void findAllAlerts(@Suspended final AsyncResponse response) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        Collection<Alert> alertList = alertsService.checkAlert();
        if (alertList.isEmpty()) {
            LOG.info("GET - findAllAlerts - Empty");
            response.resume(Response.status(Response.Status.NO_CONTENT).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.info("GET - findAllAlerts - " + alertList.size() + " entries");
            response.resume(Response.status(Response.Status.OK).entity(alertList).type(APPLICATION_JSON_TYPE).build());
        }
    }
}
