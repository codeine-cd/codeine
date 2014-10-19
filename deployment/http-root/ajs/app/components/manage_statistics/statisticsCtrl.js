(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function statisticsCtrl($scope, statistics, LoginService) {
        $scope.statsitics  = statistics;

        $scope.setViewAs = function() {
            LoginService.setViewAs($scope.newViewAs);
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('statisticsCtrl',statisticsCtrl);

})(angular);