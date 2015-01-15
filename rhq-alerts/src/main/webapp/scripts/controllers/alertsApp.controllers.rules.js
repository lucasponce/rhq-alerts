angular.module('alertsApp.controllers.rules', ['alertsApp.services'])
    .controller('RulesCtrl', function ($scope, $state, $log, $window, rulesService) {

        $scope.alerts = [];

        var getRules = function() {
            rulesService.getRules().then(function (rules) {
                $scope.rules = rules;
            }, function error(reason) {
                var newAlert = {type: 'danger', msg: reason.statusText};
                $scope.alerts.push(newAlert);
            });
        }

        getRules();

        $scope.refreshRules = function() {
            getRules();
        }

        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        }

    })
    .controller('RulesViewCtrl', function ($scope, $state, $stateParams, $log, $window, rulesService) {

        $scope.alerts = [];

        rulesService.getRule($stateParams.id).then(function (rule) {
            $scope.rule = rule;
        }, function error(reason) {
            var newAlert = {type: 'danger'};
            if (reason.data.errorMsg) {
                newAlert.msg = reason.data.errorMsg;
            } else {
                newAlert.msg = reason.statusText;
            }
            $scope.alerts.push(newAlert);
        });

        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        }

    });

