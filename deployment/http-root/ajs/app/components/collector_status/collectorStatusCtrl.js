(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function collectorStatusCtrl($routeParams, collectorStatus, $log ) {
        /*jshint validthis:true */

        var vm = this;
        vm.projectName = $routeParams.project_name;
        vm.collectorStatus = collectorStatus;
        $log.debug('collectorStatusCtrl: collectorStatus ' + angular.toJson(collectorStatus));
    }

    //// Angular Code ////
    angular.module('codeine').controller('collectorStatusCtrl',  collectorStatusCtrl);

})(angular);