(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function statisticsCtrl($scope, statistics) {
        $scope.statsitics  = statistics;
    }


    //// Angular Code ////
    angular.module('codeine').controller('statisticsCtrl',statisticsCtrl);

})(angular);