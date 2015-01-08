angular.module('alertsApp.controllers.triggers', ['alertsApp.services'])
    
    .controller('TriggersCtrl', function ($scope, $state, $log, $window, triggersService) {
        
        $scope.alerts = [];
        
        var getTriggers = function() {
            triggersService.getTriggers().then(function (triggers) {
                $log.log("getTriggers - triggers: " + triggers.length);
                $scope.triggers = triggers;
            }, function error(reason) {
                var newAlert = {type: 'danger', msg: reason.statusText};
                $scope.alerts.push(newAlert);
            });
        }
        
        getTriggers();
        
        $scope.refreshTriggers = function() {
            getTriggers();
        }
        
        $scope.deleteTrigger = function(trigger) {
            if ($window.confirm('Do you want to delete ' + trigger.id + ' ?')) {
                trigger.remove().then(function() {
                    $log.log('deleteTrigger() - remove() ' + trigger.id);
                    getTriggers();
                    $state.go('triggers');
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
    .controller('TriggerViewCtrl', function ($scope, $state, $stateParams, $log, $window, triggersService) {

        $scope.alerts = [];
        
        triggersService.getTrigger($stateParams.id).then(function (trigger) {
            $scope.trigger = trigger;
        }, function error(reason) {
            var newAlert = {type: 'danger'};
            if (reason.data.errorMsg) {
                newAlert.msg = reason.data.errorMsg;
            } else {
                newAlert.msg = reason.statusText;
            }
            $scope.alerts.push(newAlert);    
        });
        
        $scope.updateTrigger = function () {
            $scope.trigger.put().then(function() {
                $state.go('triggers');
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
        
        $scope.deleteTrigger = function() {
            if ($window.confirm('Do you want to delete ' + $scope.trigger.id + ' ?')) {
                $scope.trigger.remove().then(function() {
                    $state.go('triggers');
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
    .controller('TriggerNewCtrl', function ($scope, $state, $log, triggersService) {
        
        $scope.trigger = {};
        $scope.alerts = [];
        
        $scope.newTrigger = function() {
            triggersService.newTrigger($scope.trigger).then(function(trigger) {
                $state.go('triggers');
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

