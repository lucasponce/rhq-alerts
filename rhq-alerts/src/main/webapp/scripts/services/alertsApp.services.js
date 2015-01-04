angular.module('alertsApp.services', [])
    
    .factory('alertsService', function ($http) {
        
        var getAlerts = function() {
            return $http({
                method: 'GET',
                url: 'api/alerts'
            });
        }

        return {
            getAlerts: getAlerts
        };
        
    });