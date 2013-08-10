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

    // Preset some default outdoor hours...
    $scope.hour = new Array();
    var defaultOutdoorHours = [6,7,8,9,12,13,17,18,19];
    _.forEach(defaultOutdoorHours, function(hour) {
        $scope.hour[hour] = true;
    });

    $scope.advice = "Rain Jacket, Jacket, Jumper and scarf";

    $scope.explanation = "";

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
        recalculateAdvice();
    }

    function recalculateAdvice() {
        console.log("Recalculating advice");
        var details = getWeatherDetailsForOutdoorPeriod();
        if (details == undefined) {
            $scope.advice = "No advice available - please select a location"
            $scope.explanation = "No weather details"
        } else {
            $scope.explanation = "minTemp: " + details.minTemp + "C, maxTemp: " + details.maxTemp +
                "C, maxRainProb: " + details.maxRain + "%, maxWind: " + details.maxWind +
                "mph, maxUV: " + details.maxUV;
            var advice = calculateAdvice(details.maxRain, details.maxWind, details.maxTemp, details.maxUV);
            $scope.advice = advice;
        }
    }

    function getWeatherDetailsForOutdoorPeriod() {
        var now = new Date();
        var today = dateToDayTime(now);
        // If it's after 6pm start planning for tomorrow...
        if (now.getHours() > 18) today = today + (1000 * 60 * 60 * 24);
        var weathers = $scope.weathers;
        // Take all the weather information and filter out only those entries for the current day...
        var todaysWeather = _.filter(weathers, function(weather) {
            var d = dateToDayTime(new Date(Date.parse(weather.date)));
            return d === today;
        });
        // Filter out only those hour entries currently ticked...
        var selectedHours = _.filter(todaysWeather, function(weather) {
            var startHour = Number(weather.hours) - 1;
            var endHour = Number(weather.hours) + 1;
            var hourSlice = $scope.hour.slice(startHour, endHour);
            return _.any(hourSlice);
        });
        // Take the data with the meaningless keys that come from MetOffice and replace them with meaningful ones...
        var convertedKeys = _.map(selectedHours, function(weather) {
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
        // Set starting values from which to determine minimums and maximums from the real data...
        var accumulator = {
            minTemp: Number.MAX_VALUE,
            maxTemp: Number.MIN_VALUE,
            maxRain: Number.MIN_VALUE,
            maxWind: Number.MIN_VALUE,
            maxUV: Number.MIN_VALUE
        }
        // Go through all the relevant weather and establish the maximum and minimums...
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
