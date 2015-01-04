angular.module('alertsApp.controllers', ['alertsApp.services'])

    .controller('DashboardCtrl', function ($scope, $log, $interval, alertsService) {

        $scope.isConfigCollapsed = true;
        $scope.refreshInterval = 2000;

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
            alertsService.getAlerts()
                .success(function (data) {
                    $log.log("getAlerts() - alerts: " + data.length);
                    var alertsLength = data.length;
                    for (var i = 0; i < alertsLength; i++) {
                        var alert = data[i];
                        alert.name = alert.triggerId;
                        alert.date = new Date(alert.time);
                        // Create a description
                        alert.description = alert.matches.toString();
                        Dashboard.addEvent(alert);
                    }
                })
                .error(function (status) {
                    $log.log("getAlerts() - error - status: " + status);
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
        }

    }).controller('TriggersCtrl', function ($log) {

        $log.log('TriggersCtrl');

    }).controller('ThresholdsCtrl', function ($log) {

        $log.log('ThresholdsCtrl');

    }).controller('RulesCtrl', function ($log) {

        $log.log('RulesCtrl');

    });
