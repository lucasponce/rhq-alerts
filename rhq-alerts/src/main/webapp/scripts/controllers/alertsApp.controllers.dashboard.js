angular.module('alertsApp.controllers.dashboard', ['alertsApp.services'])

    .controller('DashboardCtrl', function ($scope, $log, $interval, alertsService) {

        $scope.alertsErrors = [];
        
        $scope.isConfigCollapsed = true;
        $scope.refreshInterval = Dashboard.getRefreshInterval();
        
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
                var newAlert = {type: 'danger'};
                if (reason.data.errorMsg) {
                    newAlert.msg = reason.data.errorMsg;
                } else {
                    newAlert.msg = reason.statusText;
                }
                $scope.alertsErrors.push(newAlert);
            });
        }
        
        var stopInterval = $interval( updateGraph, $scope.refreshInterval );
        
        $scope.$on('$destroy', function() {
            $interval.cancel(stopInterval);
        });
        
        $scope.updateRefresh = function() {
            $interval.cancel(stopInterval);
            stopInterval = $interval( updateGraph, $scope.refreshInterval );
            Dashboard.setRefreshInterval($scope.refreshInterval);
            $scope.isConfigCollapsed = true;
        }

        $scope.reloadDefinitions = function() {
            $log.log("reloadDefinitions()");
            alertsService.reload().then(function() {
                
            }, function error(reason) {
                var newAlert = {type: 'danger'};
                if (reason.data.errorMsg) {
                    newAlert.msg = reason.data.errorMsg;
                } else {
                    newAlert.msg = reason.statusText;
                }
                $scope.alertsErrors.push(newAlert);
            });
        }
        
        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        }

    });