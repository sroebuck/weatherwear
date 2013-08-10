var myApp = angular.module('weatherproof', ['ui.bootstrap', 'ngResource']);

// ECMA Script 5 style keys function definition...
if (!Object.keys) Object.keys = function(o) {
    if (o !== Object(o)) throw new TypeError('Object.keys called on non-object');
    var ret = [];
    var p;
    for (p in o) if (Object.prototype.hasOwnProperty.call(o,p)) ret.push(p);
    return p;
}

function OverallController($scope, $http) {

    $scope.selectedLocation = undefined;

    $scope.hour = [];

    $scope.advice = "Rain Jacket, Jacket, Jumper and scarf";

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
                recalculateAdvice();
            });

        }
    }

    function changedHours(newHours, oldHours, scope) {
        var ticks = 0;
        _.forEach(scope.hour, function(h) {
            if (h) ticks = ticks + 1;
        });        
        scope.testValue = ticks;
        recalculateAdvice();
    }

    function recalculateAdvice() {
        var details = getWeatherDetailsForOutdoorPeriod();
        if (details == undefined) $scope.advice = "No advice available - please select a location"
        else {
            console.log(details);
            var advice = calculateAdvice(details.maxRain, details.maxWind, details.maxTemp, details.maxUV);
            $scope.advice = advice;
        }
    }

    function getWeatherDetailsForOutdoorPeriod() {
        var today = dateToDayTime(new Date());
        var weathers = $scope.weathers;
        var todaysWeather = _.filter(weathers, function(weather) {
            var d = dateToDayTime(new Date(Date.parse(weather.date)));
            return d === today;
        });
        var convertedKeys = _.map(todaysWeather, function(weather) {
            return {
                date: weather.date,
                hour: weather.hour,
                temp: weather.T,
                rain: weather.Pp,
                wind: weather.S,
                gust: weather.G,
                UV: weather.U
            };
        });
        var accumulator = {
            minTemp: Number.MAX_VALUE,
            maxTemp: Number.MIN_VALUE,
            maxRain: Number.MIN_VALUE,
            maxWind: Number.MIN_VALUE,
            maxUV: Number.MIN_VALUE
        }
        var summary = _.reduce(convertedKeys, function(acc, weather) {
            acc.minTemp = Math.min(acc.minTemp, weather.temp);
            acc.maxTemp = Math.max(acc.maxTemp, weather.temp);
            acc.maxRain = Math.max(acc.maxRain, weather.rain);
            acc.maxWind = Math.max(acc.maxWind, weather.wind);
            acc.maxUV = Math.max(acc.maxUV, weather.UV);
            return acc;
        }, accumulator);
        // If there are no weather results for today then return an undefined value otherwise return the results...
        if (todaysWeather.length == 0) return undefined
        else return summary;
    }

    function dateToDayTime(date) {
        var day = new Date(date.getYear(), date.getMonth(), date.getDate());
        return day.getTime();
    }

    $scope.$watch('hour', changedHours, true);

    $scope.$watch('selectedLocation', changedLocation);

}
