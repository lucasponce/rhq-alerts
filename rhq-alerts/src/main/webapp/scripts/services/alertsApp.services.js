angular.module('alertsApp.services', [])
    
    .factory('alertsService', ['Restangular', function (Restangular) {

        var alerts = Restangular.all('alerts');
        
        var getAlerts = function() {
            return alerts.getList();
        }

        var reload = function() {
            return Restangular.one('reload').get();
        }
        
        return {
            getAlerts: getAlerts,
            reload: reload
        };

    }])
    .factory('triggersService', ['Restangular', function (Restangular) {
        
        var triggers = Restangular.all('triggers');

        var getTriggers = function() {
            return triggers.getList();
        }
        
        var getTrigger = function(triggerId) {
            return Restangular.one('triggers', triggerId).get();
        }
        
        var newTrigger = function(newTrigger) {
            return triggers.post(newTrigger);
        }
        
        return  {
            getTriggers: getTriggers, 
            getTrigger: getTrigger,
            newTrigger: newTrigger
        };
        
    }]).factory('thresholdsService', ['Restangular', function (Restangular) {

        var thresholds = Restangular.all('thresholds');

        var getThresholds = function() {
            return thresholds.getList();
        }
        
        var getThreshold = function(triggerId, metricId) {
            return Restangular.one('thresholds').one('t', triggerId).one('m', metricId).get();
        }
        
        var newThreshold = function(newThreshold) {
            return thresholds.post(newThreshold);
        }
        
        var updateThreshold = function(threshold) {
            return Restangular.one('thresholds').one('t', threshold.triggerId).one('m', threshold.metricId)
                .customPUT(threshold);
        }

        var deleteThreshold = function(threshold) {
            return Restangular.one('thresholds').one('t', threshold.triggerId).one('m', threshold.metricId)
                .customDELETE();
        }
        
        return  {
            getThresholds: getThresholds,
            getThreshold: getThreshold,
            updateThreshold: updateThreshold,
            deleteThreshold: deleteThreshold,
            newThreshold: newThreshold
        };

    }]).factory('rulesService', ['Restangular', function (Restangular) {

        var rules = Restangular.all('rules');

        var getRules = function() {
            return rules.getList();
        }

        var getRule = function(ruleId) {
            return Restangular.one('rules', ruleId).get();
        }

        var newRule = function(newRule) {
            return rules.post(newRule);
        }        
        
        return  {
            getRules: getRules,
            getRule: getRule,
            newRule: newRule
        };

    }]);