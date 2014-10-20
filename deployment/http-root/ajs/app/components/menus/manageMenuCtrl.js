(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function manageMenuCtrl($scope,$log,CodeineService,LoginService) {
        $log.debug('manageMenuCtrl: created');
        $scope.isPrepareForShutdown = LoginService.getSessionInfo().isPrepareForShutdown;
        $scope.prepareForShutdown = function() {
            $log.debug('manageMenuCtrl: prepareForShutdown');
            CodeineService.prepareForShutdown().success(function() {
                $log.debug('prepareForShutdown - success');
                LoginService.getSessionInfo().isPrepareForShutdown = true;
            });
        };
        $scope.cancelShutdown = function() {
            $log.debug('manageMenuCtrl: cancelShutdown');
            CodeineService.cancelShutdown().success(function() {
                $log.debug('cancelShutdown - success');
                LoginService.getSessionInfo().isPrepareForShutdown = false;
            });
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('manageMenuCtrl', manageMenuCtrl);

})(angular);