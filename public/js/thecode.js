var myApp = angular.module('weatherproof', ['ui.bootstrap', 'ngResource']);

function TypeaheadCtrl($scope, $http) {
    $scope.selected = undefined;

    // Download the list of locations
    $http.get('/api/locations').success(function(json) {
        $scope.states = json
    });

}



