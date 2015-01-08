package org.rhq.alerts.rest;

import org.rhq.alerts.api.common.condition.Alert;
import org.rhq.alerts.api.common.trigger.Trigger;
import org.rhq.alerts.api.services.AlertsService;
import org.rhq.alerts.api.services.DefinitionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 TODO
 */
@Path("/")
public class AlertsHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AlertsHandler.class);
    
    @Inject
    AlertsService alertsService;
    
    @Inject
    DefinitionsService definitionsService;
    
    @GET
    @Path("/alerts")
    @Produces(APPLICATION_JSON)
    public void findAllAlerts(@Suspended final AsyncResponse response) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        Collection<Alert> alertList = alertsService.checkAlert();
        if (alertList.isEmpty()) {
            LOG.debug("GET - findAllAlerts - Empty");
            response.resume(Response.status(Response.Status.NO_CONTENT).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.debug("GET - findAllAlerts - " + alertList.size() + " alerts");
            response.resume(Response.status(Response.Status.OK).entity(alertList).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @GET
    @Path("/triggers")
    @Produces(APPLICATION_JSON)
    public void findAllTriggers(@Suspended final AsyncResponse response) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        Collection<Trigger> triggerList = definitionsService.getTriggers();
        if (triggerList.isEmpty()) {
            LOG.debug("GET - findAllTriggers - Empty");
            response.resume(Response.status(Response.Status.NO_CONTENT).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.debug("GET - findAllTriggers - " + triggerList.size() + " triggers");
            response.resume(Response.status(Response.Status.OK).entity(triggerList).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @POST
    @Path("/triggers")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public void createTrigger(@Suspended final AsyncResponse response, final Trigger trigger) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        if (trigger != null && trigger.getId() != null && definitionsService.getTrigger(trigger.getId()) == null) {
            LOG.debug("POST - createTrigger - triggerId " + trigger.getId());
            definitionsService.addTrigger(trigger);
            response.resume(Response.status(Response.Status.OK).entity(trigger).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.debug("POST - createTrigger - ID not valid or existing trigger");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Existing trigger or invalid ID");
            response.resume(Response.status(Response.Status.BAD_REQUEST).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @GET
    @Path("/triggers/{triggerId}")
    @Produces(APPLICATION_JSON)
    public void getTrigger(@Suspended final AsyncResponse response, @PathParam("triggerId") final String triggerId) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */                
        Trigger found = null;
        if (triggerId != null && !triggerId.isEmpty()) {
            found = definitionsService.getTrigger(triggerId);
        }
        if (found != null) {
            LOG.debug("GET - getTrigger - triggerId: " + found.getId());
            response.resume(Response.status(Response.Status.OK).entity(found).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.debug("GET - getTrigger - triggerId : " + triggerId + " not found or invalid. ");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Trigger ID " + triggerId + " not found or invalid ID");
            response.resume(Response.status(Response.Status.NOT_FOUND).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @PUT
    @Path("/triggers/{triggerId}")
    @Consumes(APPLICATION_JSON)
    public void updateTrigger(@Suspended final AsyncResponse response, @PathParam("triggerId") final String triggerId,
                              final Trigger trigger) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        if (triggerId != null && !triggerId.isEmpty() &&
                trigger != null && trigger.getId() != null &&
                triggerId.equals(trigger.getId()) && 
                definitionsService.getTrigger(triggerId) != null) {
            LOG.debug("PUT - updateTrigger - triggerId: " + triggerId);
            definitionsService.removeTrigger(triggerId);
            definitionsService.addTrigger(trigger);
            response.resume(Response.status(Response.Status.OK).build());
        } else {
            LOG.debug("PUT - updateTrigger - triggerId: " + triggerId + " not found or invalid. ");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Trigger ID " + triggerId + " not found or invalid ID");
            response.resume(Response.status(Response.Status.NOT_FOUND).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @DELETE
    @Path("/triggers/{triggerId}")
    public void deleteTrigger(@Suspended final AsyncResponse response, @PathParam("triggerId") final String triggerId) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        if (triggerId != null && !triggerId.isEmpty() && definitionsService.getTrigger(triggerId) != null) {
            LOG.debug("DELETE - deleteTrigger - triggerId: " + triggerId);
            definitionsService.removeTrigger(triggerId);
            response.resume(Response.status(Response.Status.OK).build());
        } else {
            LOG.debug("DELETE - deleteTrigger - triggerId: " + triggerId + " not found or invalid. ");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Trigger ID " + triggerId + " not found or invalid ID");
            response.resume(Response.status(Response.Status.NOT_FOUND).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
}
