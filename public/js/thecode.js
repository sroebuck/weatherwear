var myApp = angular.module('weatherproof', ['ui.bootstrap', 'ngResource']);

myApp.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}]);


function TypeaheadCtrl($scope, $http) {
    $scope.selected = undefined;
    $scope.states = ['Edinburgh', 'Glasgow', 'Aberdeen', 'Dundee'];
    // var locationsResource = $resource('http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/sitelist',
    // 	{key: keyString}, {method: 'GET'});
    getLocationsList();


    function getLocationsList() {
        var promise = $http.get('http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/sitelist', {
        	params: {key: keyString}
        });
        promise.success(function(json) {
        	console.log(json);
        });
    }


}

