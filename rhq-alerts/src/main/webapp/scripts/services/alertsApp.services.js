angular.module('alertsApp.services', [])
    
    .factory('alertsService', ['Restangular', function (Restangular) {

        var alerts = Restangular.all('alerts');
        
        var getAlerts = function() {
            return alerts.getList();
        }

        return {
            getAlerts: getAlerts
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
        
    }]);