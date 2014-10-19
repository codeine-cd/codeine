(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function manageMenuCtrl($scope,$rootScope,$log,CodeineService) {
        $log.debug('manageMenuCtrl: created');
        $scope.isPrepareForShutdown = $rootScope.app.sessionInfo.isPrepareForShutdown;
        $scope.prepareForShutdown = function() {
            $log.debug('manageMenuCtrl: prepareForShutdown');
            CodeineService.prepareForShutdown().success(function(data) {
                $log.debug('prepareForShutdown - success');
                $log.debug('run: got session info ' + angular.toJson(data));
                $rootScope.app.sessionInfo = data;
                $scope.isPrepareForShutdown = $rootScope.app.sessionInfo.isPrepareForShutdown;
            });
        };
        $scope.cancelShutdown = function() {
            $log.debug('manageMenuCtrl: cancelShutdown');
            CodeineService.cancelShutdown().success(function(data) {
                $log.debug('cancelShutdown - success');
                $log.debug('run: got session info ' + angular.toJson(data));
                $rootScope.app.sessionInfo = data;
                $scope.isPrepareForShutdown = $rootScope.app.sessionInfo.isPrepareForShutdown;
            });
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('manageMenuCtrl', manageMenuCtrl);

})(angular);