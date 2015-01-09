package org.rhq.alerts.rest;

import com.google.common.collect.ImmutableMap;
import org.rhq.alerts.api.common.condition.Alert;
import org.rhq.alerts.api.common.condition.ThresholdCondition;
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
import java.util.*;

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
    
    @GET
    @Path("/thresholds")
    @Produces(APPLICATION_JSON)
    public void findAllThresholds(@Suspended final AsyncResponse response) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        Collection<ThresholdCondition> thresholdsList = definitionsService.getThresholds();
        if (thresholdsList.isEmpty()) {
            LOG.debug("GET - findAllThresholds - Empty");
            response.resume(Response.status(Response.Status.NO_CONTENT).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.debug("GET - findAllThresholds - " + thresholdsList.size() + " thresholds");
            response.resume(Response.status(Response.Status.OK).entity(thresholdsList).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @POST
    @Path("/thresholds")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public void createThreshold(@Suspended final AsyncResponse response, final ThresholdCondition threshold) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        if (threshold != null && threshold.getTriggerId() != null && threshold.getMetricId() != null &&
                    definitionsService.getThreshold(threshold.getTriggerId(), threshold.getMetricId()) == null) {
            LOG.debug("POST - createThreshold - triggerId " + threshold.getTriggerId() + 
                              " - metricId " + threshold.getMetricId());
            definitionsService.addThreshold(threshold);
            response.resume(Response.status(Response.Status.OK).entity(threshold).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.debug("POST - createThreshold - ID not valid or existing threshold");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Existing threshold or invalid ID");
            response.resume(Response.status(Response.Status.BAD_REQUEST).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @GET
    @Path("/thresholds/t/{triggerId}/m/{metricId}")
    @Produces(APPLICATION_JSON)
    public void getThreshold(@Suspended final AsyncResponse response, @PathParam("triggerId") final String triggerId,
                             @PathParam("metricId") final String metricId) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        ThresholdCondition found = null;
        if (triggerId != null && !triggerId.isEmpty() &&
                metricId != null && !metricId.isEmpty()) {
            found = definitionsService.getThreshold(triggerId, metricId);
        }
        if (found != null) {
            LOG.debug("GET - getThreshold - triggerId: " + found.getTriggerId() + " - metricId: " + found.getMetricId());
            response.resume(Response.status(Response.Status.OK).entity(found).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.debug("GET - getThreshold - triggerId : " + triggerId + " - metricId: " + metricId + 
                              " not found or invalid. ");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Threshold triggerId: " + triggerId + " - metricId: " + metricId + 
                                           " not found or invalid ID");
            response.resume(Response.status(Response.Status.NOT_FOUND).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @PUT
    @Path("/thresholds/t/{triggerId}/m/{metricId}")
    @Consumes(APPLICATION_JSON)
    public void updateThreshold(@Suspended final AsyncResponse response, @PathParam("triggerId") final String triggerId,
                                @PathParam("metricId") final String metricId, final ThresholdCondition threshold) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        if (triggerId != null && !triggerId.isEmpty() &&
                    metricId != null && !metricId.isEmpty() &&
                    threshold != null &&
                    threshold.getTriggerId() != null && threshold.getTriggerId().equals(triggerId) &&
                    threshold.getMetricId() != null && threshold.getMetricId().equals(metricId) &&
                    definitionsService.getThreshold(triggerId, metricId) != null) {
            LOG.debug("PUT - updateThreshold - triggerId: " + triggerId + " metricId: " + metricId);
            definitionsService.removeThreshold(triggerId, metricId);
            definitionsService.addThreshold(threshold);
            response.resume(Response.status(Response.Status.OK).build());
        } else {
            LOG.debug("PUT - updateThreshold - triggerId: " + triggerId + " - metricId: " + metricId + 
                              " not found or invalid. ");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Threshold triggerId: " + triggerId + " - metricId: " + metricId + 
                                           " not found or invalid ID");
            response.resume(Response.status(Response.Status.NOT_FOUND).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @DELETE
    @Path("/thresholds/t/{triggerId}/m/{metricId}")
    public void deleteThreshold(@Suspended final AsyncResponse response, @PathParam("triggerId") final String triggerId,
                                @PathParam("metricId") final String metricId) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        if (triggerId != null && !triggerId.isEmpty() &&
                    metricId != null && !metricId.isEmpty() &&
                    definitionsService.getThreshold(triggerId, metricId) != null) {
            LOG.debug("DELETE - deleteThreshold - triggerId: " + triggerId + " metricId: " + metricId);
            definitionsService.removeThreshold(triggerId, metricId);
            response.resume(Response.status(Response.Status.OK).build());
        } else {
            LOG.debug("DELETE - deleteThreshold - triggerId: " + triggerId + " - metricId: " + metricId + 
                              " not found or invalid. ");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Threshold triggerId: " + triggerId + " - metricId: " + metricId +
                                           " not found or invalid ID");
            response.resume(Response.status(Response.Status.NOT_FOUND).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @GET
    @Path("/rules")
    @Produces(APPLICATION_JSON)
    public void findAllRules(@Suspended final AsyncResponse response) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        Map<String, String> rulesMap = definitionsService.getRules();
        if (rulesMap.isEmpty()) {
            LOG.debug("GET - findAllRules - Empty");
            response.resume(Response.status(Response.Status.NO_CONTENT).type(APPLICATION_JSON_TYPE).build());
        } else {
            List<Map<String, String>> rulesList = new ArrayList();
            for (String key : rulesMap.keySet()) {
                Map<String, String> rule = new HashMap<String, String>();
                rule.put("id", key);
                rule.put("rule", rulesMap.get(key));
                rulesList.add(rule);
            }
            LOG.debug("GET - findAllRules - " + rulesList.size() + " rules");
            response.resume(Response.status(Response.Status.OK).entity(rulesList).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @POST
    @Path("/rules")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public void createRule(@Suspended final AsyncResponse response, final Map<String, String> rule) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        if (rule != null && rule.get("id") != null &&
                    definitionsService.getRule(rule.get("id")) == null) {
            LOG.debug("POST - createRule - id " + rule.get("id"));
            definitionsService.addRule(rule.get("id"), rule.get("rule"));
            response.resume(Response.status(Response.Status.OK).entity(rule).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.debug("POST - createRule - ID not valid or existing rule");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Existing rule or invalid ID");
            response.resume(Response.status(Response.Status.BAD_REQUEST).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }

    @GET
    @Path("/rules/{ruleId}")
    @Produces(APPLICATION_JSON)
    public void getRule(@Suspended final AsyncResponse response, @PathParam("ruleId") final String ruleId) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        String found = null;
        if (ruleId != null && !ruleId.isEmpty()) {
            found = definitionsService.getRule(ruleId);
        }
        if (found != null) {
            LOG.debug("GET - getRule - ruleId: " + ruleId);
            Map<String, String> rule = new HashMap<String, String>();
            rule.put("id", ruleId);
            rule.put("rule", found);
            response.resume(Response.status(Response.Status.OK).entity(rule).type(APPLICATION_JSON_TYPE).build());
        } else {
            LOG.debug("GET - getRule - ruleId : " + ruleId + " not found or invalid. ");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Rule ruleId: " + ruleId + " not found or invalid ID");
            response.resume(Response.status(Response.Status.NOT_FOUND).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @PUT
    @Path("/rules/{ruleId}")
    @Consumes(APPLICATION_JSON)
    public void updateRule(@Suspended final AsyncResponse response, @PathParam("ruleId") final String ruleId,
                                final Map<String, String> rule) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        if (ruleId != null && !ruleId.isEmpty() &&
                    rule.get("id") != null && rule.get("id").equals(ruleId) &&
                    definitionsService.getRule(ruleId) != null) {
            LOG.debug("PUT - updateRule - ruleId: " + ruleId );
            definitionsService.removeRule(ruleId);
            definitionsService.addRule(ruleId, rule.get("rule"));
            response.resume(Response.status(Response.Status.OK).build());
        } else {
            LOG.debug("PUT - updateRule - ruleId: " + ruleId + " not found or invalid. ");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Rule ruleId: " + ruleId + " not found or invalid ID");
            response.resume(Response.status(Response.Status.NOT_FOUND).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @DELETE
    @Path("/rules/{ruleId}")
    public void deleteRule(@Suspended final AsyncResponse response, @PathParam("ruleId") final String ruleId) {
        /*
            TODO Add support for ListenableFuture and Futures in the main APIs
         */
        if (ruleId != null && !ruleId.isEmpty() &&
                    definitionsService.getRule(ruleId) != null) {
            LOG.debug("DELETE - deleteRule - ruleId: " + ruleId);
            definitionsService.removeRule(ruleId);
            response.resume(Response.status(Response.Status.OK).build());
        } else {
            LOG.debug("DELETE - deleteRule - ruleId: " + ruleId + " not found or invalid. ");
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("errorMsg", "Rule ruleId: " + ruleId + " not found or invalid ID");
            response.resume(Response.status(Response.Status.NOT_FOUND).entity(errors).type(APPLICATION_JSON_TYPE).build());
        }
    }
    
    @GET
    @Path("/reload")
    public void reloadAlerts(@Suspended final AsyncResponse response) {
        alertsService.reload();
        response.resume(Response.status(Response.Status.OK).build());
    }
    
}
