(function (angular) {
    'use strict';

    //// JavaScript Code ////
    function monitorStatusCtrl($scope, $log,$routeParams, monitorStatus ) {
        $scope.projectName = $routeParams.project_name;
        $log.debug('monitorStatusCtrl: current project is ' + $scope.projectName);
        $scope.monitorStatus = monitorStatus;

    }

    //// Angular Code ////
    angular.module('codeine').controller('monitorStatusCtrl',  monitorStatusCtrl);

})(angular);