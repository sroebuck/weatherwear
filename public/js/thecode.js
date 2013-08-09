var myApp = angular.module('weatherproof', ['ui.bootstrap', 'ngResource']);

// ECMA Script 5 style keys function definition...
if (!Object.keys) Object.keys = function(o) {
    if (o !== Object(o)) throw new TypeError('Object.keys called on non-object');
    var ret = [];
    var p;
    for (p in o) if (Object.prototype.hasOwnProperty.call(o,p)) ret.push(p);
    return p;
}

function TypeaheadCtrl($scope, $http) {
    $scope.selectedLocation = undefined;

    // Download the list of locations
    $http.get('/api/locations').success(function(json) {
        $scope.locationsMap = json;
        $scope.locations = Object.keys($scope.locationsMap).sort()
    });


    function changedLocation(newLocation, oldLocation, scope) {
        var isValidLocation = _.contains($scope.locations, newLocation);
        if (isValidLocation) {
            var locationId = $scope.locationsMap[$scope.selectedLocation];
            $http.get('/api/weather/' + locationId).success(function(json) {
                $scope.weathers = json;
                console.log($scope.weathers);
            });

        }
    }

    $scope.$watch('selectedLocation', changedLocation);

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
