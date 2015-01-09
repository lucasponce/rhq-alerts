angular.module('alertsApp.controllers.thresholds', ['alertsApp.services'])
    .controller('ThresholdsCtrl', function ($scope, $state, $log, $window, thresholdsService) {

        $scope.alerts = [];

        var getThresholds = function() {
            thresholdsService.getThresholds().then(function (thresholds) {
                $scope.thresholds = thresholds;
            }, function error(reason) {
                var newAlert = {type: 'danger', msg: reason.statusText};
                $scope.alerts.push(newAlert);
            });
        }

        getThresholds();

        $scope.refreshThresholds = function() {
            getThresholds();
        }

        $scope.deleteThreshold = function(threshold) {
            if ($window.confirm('Do you want to delete ' + threshold.triggerId + ' / ' + threshold.metricId + ' ?')) {
                thresholdsService.deleteThreshold(threshold).then(function() {
                    getThresholds();
                    $state.go('thresholds');
                }, function error(reason) {
                    var newAlert = {type: 'danger'};
                    if (reason.data.errorMsg) {
                        newAlert.msg = reason.data.errorMsg;
                    } else {
                        newAlert.msg = reason.statusText;
                    }
                    $scope.alerts.push(newAlert);
                });
            }
        }

        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        }


    })
    .controller('ThresholdsViewCtrl', function ($scope, $state, $stateParams, $log, $window, thresholdsService) {

        $scope.alerts = [];

        thresholdsService.getThreshold($stateParams.triggerId, $stateParams.metricId).then(function (threshold) {
            $scope.threshold = threshold;
        }, function error(reason) {
            var newAlert = {type: 'danger'};
            if (reason.data.errorMsg) {
                newAlert.msg = reason.data.errorMsg;
            } else {
                newAlert.msg = reason.statusText;
            }
            $scope.alerts.push(newAlert);
        });

        $scope.updateThreshold = function () {
            thresholdsService.updateThreshold($scope.threshold).then(function() {
                $state.go('thresholds');
            }, function error(reason) {
                var newAlert = {type: 'danger'};
                if (reason.data.errorMsg) {
                    newAlert.msg = reason.data.errorMsg;
                } else {
                    newAlert.msg = reason.statusText;
                }
                $scope.alerts.push(newAlert);
            });
        }

        $scope.deleteThreshold = function() {
            if ($window.confirm('Do you want to delete ' + $scope.threshold.triggerId + '/' 
                + $scope.threshold.metricId + ' ?')) {
                thresholdsService.deleteThreshold($scope.threshold).then(function() {
                    $state.go('thresholds');
                }, function error(reason) {
                    var newAlert = {type: 'danger'};
                    if (reason.data.errorMsg) {
                        newAlert.msg = reason.data.errorMsg;
                    } else {
                        newAlert.msg = reason.statusText;
                    }
                    $scope.alerts.push(newAlert);
                });
            }
        }

        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        }

    })
    .controller('ThresholdsNewCtrl', function ($scope, $state, $log, thresholdsService) {

        /*
            Init new threshold object with default match value
         */
        $scope.threshold = { operator: "GT" };
        $scope.alerts = [];

        $scope.newThreshold = function() {
            thresholdsService.newThreshold($scope.threshold).then(function(threshold) {
                $state.go('thresholds');
            }, function error(reason) {
                var newAlert = {type: 'danger'};
                if (reason.data.errorMsg) {
                    newAlert.msg = reason.data.errorMsg;
                } else {
                    newAlert.msg = reason.statusText;
                }
                $scope.alerts.push(newAlert);
            });
        }

        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        }
    });

