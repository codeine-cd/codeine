'use strict';
angular.module('codeine').controller('statisticsCtrl',['$scope', '$log', 'statistics', function($scope, $log, statistics) {
    $scope.statsitics  = statistics;

}]);


