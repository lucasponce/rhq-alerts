angular.module('alertsApp', ['ui.bootstrap', 'ui.router', 'restangular', 'alertsApp.controllers.dashboard',
    'alertsApp.controllers.triggers', 'alertsApp.controllers.thresholds', 'alertsApp.controllers.rules']);

angular.module('alertsApp').config(function ($stateProvider) {

    /*
        Dashboard navigation
     */
    $stateProvider.state('dashboard', {
        url: '/dashboard',
        templateUrl: 'views/dashboard.html',
        controller: 'DashboardCtrl'
    });

    /*
        Triggers navigation
     */
    $stateProvider.state('triggers', {
        url: '/triggers',
        templateUrl: 'views/triggers.html',
        controller: 'TriggersCtrl'
    }).state('viewTrigger', {
        url: '/triggers/:id/view',
        templateUrl: 'views/trigger-view.html',
        controller: 'TriggerViewCtrl'
    }).state('newTrigger', {
        url: '/triggers/new',
        templateUrl: 'views/trigger-new.html',
        controller: 'TriggerNewCtrl'
    });

    /*
        Thresholds navigation
     */
    $stateProvider.state('thresholds', {
        url: '/thresholds',
        templateUrl: 'views/thresholds.html',
        controller: 'ThresholdsCtrl'
    }).state('viewThreshold', {
        url: '/thresholds/t/:triggerId/m/:metricId/view',
        templateUrl: 'views/threshold-view.html',
        controller: 'ThresholdsViewCtrl'
    }).state('newThreshold', {
        url: '/thresholds/new',
        templateUrl: 'views/threshold-new.html',
        controller: 'ThresholdsNewCtrl'
    });
    
    /*
        Rules navigation
     */
    $stateProvider.state('rules', {
        url: '/rules',
        templateUrl: 'views/rules.html',
        controller: 'RulesCtrl'
    }).state('viewRule', {
        url: '/rules/:id/view',
        templateUrl: 'views/rule-view.html',
        controller: 'RulesViewCtrl'
    });

}).config(function(RestangularProvider) {
    
    /*
        RHQ-Alerts API base url
     */
    RestangularProvider.setBaseUrl('api');
    
}).run(function ($state) {

    $state.go('dashboard');

});
