angular.module('weatherproof', ['ui.bootstrap']);
function TypeaheadCtrl($scope) {
	$scope.selected = undefined;
	$scope.states = ['Edinburgh', 'Glasgow', 'Aberdeen', 'Dundee'];
}