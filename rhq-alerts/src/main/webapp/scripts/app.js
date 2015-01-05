angular.module('alertsApp', ['ui.bootstrap', 'ui.router', 'alertsApp.controllers.dashboard',
    'alertsApp.controllers.triggers', 'alertsApp.controllers.thresholds', 'alertsApp.controllers.rules']);

angular.module('alertsApp').config(function ($stateProvider, $httpProvider) {

    $stateProvider.state('dashboard', {
        url: '/dashboard',
        templateUrl: 'views/dashboard.html',
        controller: 'DashboardCtrl'
    }).state('triggers', {
        url: '/triggers',
        templateUrl: 'views/triggers.html',
        controller: 'TriggersCtrl'
    }).state('thresholds', {
        url: '/thresholds',
        templateUrl: 'views/thresholds.html',
        controller: 'ThresholdsCtrl'
    }).state('rules', {
        url: '/rules',
        templateUrl: 'views/rules.html',
        controller: 'RulesCtrl'
    });

}).run(function ($state) {

    $state.go('dashboard');

});
