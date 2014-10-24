(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function manageMenuCtrl(LoginService) {
        /*jshint validthis:true */
        var vm = this;

        vm.sessionInfo = LoginService.getSessionInfo();

        vm.prepareForShutdown = function() {
            LoginService.prepareForShutdown();
        };

        vm.cancelShutdown = function() {
            LoginService.cancelShutdown();
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('manageMenuCtrl', manageMenuCtrl);

})(angular);