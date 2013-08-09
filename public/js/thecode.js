var myApp = angular.module('weatherproof', ['ui.bootstrap', 'ngResource']);

function TypeaheadCtrl($scope, $http) {
    $scope.selected = undefined;

    // Download the list of locations
    $http.get('/api/locations').success(function(json) {
        $scope.states = json;
    });

}

function HoursOutdoors($scope) {

    $scope.hour = [];

    function changedHours(newHours, oldHours, scope) {
        var ticks = 0;
        _.forEach(scope.hour, function(h) {
            if (h) ticks = ticks + 1;
        });        
        scope.testValue = ticks;
    }

    $scope.$watch('hour', changedHours, true);

}
