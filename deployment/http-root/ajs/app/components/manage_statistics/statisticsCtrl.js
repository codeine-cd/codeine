(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function statisticsCtrl($scope, statistics, CodeineService, $log, $location) {
        $scope.statsitics  = statistics;

        $scope.setViewAs = function() {
            $scope.app.viewAs = $scope.newViewAs;
            CodeineService.getSessionInfo().success(function(data) {
                $log.debug('run: got session info ' + angular.toJson(data));
                $scope.app.sessionInfo = data;
                $location.path('/codeine');
            }).error(function() {
                $scope.app.viewAs = null;
            });
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('statisticsCtrl',statisticsCtrl);

})(angular);