(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function monitorStatusCtrl($routeParams, monitorStatus ) {
        /*jshint validthis:true */

        var vm = this;
        vm.projectName = $routeParams.project_name;
        vm.monitorStatus = monitorStatus;
    }

    //// Angular Code ////
    angular.module('codeine').controller('monitorStatusCtrl',  monitorStatusCtrl);

})(angular);