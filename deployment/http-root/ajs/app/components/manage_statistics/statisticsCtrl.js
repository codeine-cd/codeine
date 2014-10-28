(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function statisticsCtrl(statistics, LoginService) {
        /*jshint validthis:true */
        var vm = this;

        vm.statsitics  = statistics;

        vm.setViewAs = function() {
            LoginService.setViewAs(vm.newViewAs);
        };
    }

    //// Angular Code ////
    angular.module('codeine').controller('statisticsCtrl',statisticsCtrl);

})(angular);