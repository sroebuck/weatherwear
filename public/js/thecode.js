angular.module('weatherproof', ['ui.bootstrap']);

function TypeaheadCtrl($scope, $http) {
    $scope.selected = undefined;
    $scope.states = ['Edinburgh', 'Glasgow', 'Aberdeen', 'Dundee'];
    getLocationsList();

    function getLocationsList() {
        var url = 'http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/sitelist?key=1fd60563-da77-4bb9-88d5-0444be01310f';
        $http.get(url, function(json) {
            console.log(json);
        });
    }


}

