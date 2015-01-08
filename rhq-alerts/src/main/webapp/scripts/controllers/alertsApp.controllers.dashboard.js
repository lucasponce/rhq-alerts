angular.module('alertsApp.controllers.dashboard', ['alertsApp.services'])

    .controller('DashboardCtrl', function ($scope, $log, $interval, alertsService) {

        $scope.isConfigCollapsed = true;
        $scope.refreshInterval = Dashboard.getRefreshInterval();

        $log.log('DashboardCtrl');

        var dashboardId = 'alerts_dashboard';
        var legendId = 'legend_dashboard';

        var oneHour = 1 * 60 * 60 * 1000;
        var endTime = Date.now() + oneHour;
        var startTime = endTime - (1.5 * oneHour);

        var hoverCallback = function(series, timestamp) {
            /*
                "series" var will store whole text with (<number>) counter representation
             */
            var sanitizedSeries = series.substring(0, series.lastIndexOf('(')).trim();
            $scope.$apply(function() {
                $scope.legend = Dashboard.getEvent(sanitizedSeries, timestamp);
            });
        }
         
        Dashboard.init(dashboardId,
            900,
            { top: 100, left: 125, bottom: 0, right: 0 },
            startTime,
            endTime,
            hoverCallback
        );

        function updateGraph() {
            alertsService.getAlerts().then(function (alerts) {
                $log.log("getAlerts - alerts: " + alerts.length);
                var alertsLength = alerts.length;
                for (var i = 0; i < alertsLength; i++) {
                    var alert = alerts[i];
                    alert.name = alert.triggerId;
                    alert.date = new Date(alert.time);
                    // Create a description
                    alert.description = alert.matches.toString();
                    Dashboard.addEvent(alert);
                }
            }, function error(reason) {
                $log.log("getAlerts() - error - reason: " + reason);
            });
        }
        
        var stopInterval = $interval( updateGraph, $scope.refreshInterval );
        
        $scope.$on('$destroy', function() {
            $log.log('Destroying DashboardCtrl - Cancelling $interval...');
            
            $interval.cancel(stopInterval);
        });
        
        $scope.updateRefresh = function() {
            $log.log('Updating refresh...');
            
            $interval.cancel(stopInterval);
            stopInterval = $interval( updateGraph, $scope.refreshInterval );
            Dashboard.setRefreshInterval($scope.refreshInterval);
            $scope.isConfigCollapsed = true;
        }

    });